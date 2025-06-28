package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuPlanUpdater {

    private static final int MINUTES_PER_DAY = 24 * 60;

    public void updateMenusPlans(Restaurant restaurant, RestaurantDTO restaurantDTO) {
        Settings settings = restaurant.getSettings();
        SettingsDTO settingsDTO = restaurantDTO.settings();

        // 1) pull old vs. new operating hours
        Map<DayOfWeek, TimeRange> oldOperatingHours = settings.getOperatingHours();
        Map<DayOfWeek, TimeRange> newOperatingHours = settingsDTO.operatingHours();

        // 2) if hours haven't actually changed, do nothing
        if (Objects.equals(oldOperatingHours, newOperatingHours)) {
            return;
        }

        for (DayOfWeek day : newOperatingHours.keySet()) {
            boolean wasOpenedBefore = wasOpenedBefore(oldOperatingHours, newOperatingHours, day);
            boolean wasClosedBefore = wasClosedBefore(oldOperatingHours, newOperatingHours, day);
            if (wasOpenedBefore || wasClosedBefore) {
                continue;
            }
            TimeRange newDayHours = newOperatingHours.get(day);
            TimeRange oldDayHours = oldOperatingHours.get(day);

            ClockPoint newStart = ClockPoint.start(newDayHours.getStartTime());
            ClockPoint oldStart = ClockPoint.start(oldDayHours.getStartTime());
            boolean headExtension = newStart.isBefore(oldStart);
            boolean headShrink = oldStart.isBefore(newStart);
            boolean tailExtension = normalisedEnd(newDayHours) > normalisedEnd(oldDayHours);
            boolean tailShrink = normalisedEnd(newDayHours) < normalisedEnd(oldDayHours);

            if (headExtension || headShrink) {
                handleHeadModification(newOperatingHours, restaurant, day);
            }

            if (tailExtension || tailShrink) {
                handleTailModification(newOperatingHours, oldOperatingHours, restaurant, day);
            }
        }


        for (Menu menu : restaurant.getMenus()) {
            for (Map.Entry<DayOfWeek, TimeRange> e : newOperatingHours.entrySet()) {
                DayOfWeek day = e.getKey();

                if (wasClosedBefore(oldOperatingHours, newOperatingHours, day)) {
                    createPlanForNewlyAvailableDay(menu, day, newOperatingHours);
                } else if (wasOpenedBefore(oldOperatingHours, newOperatingHours, day)) {
                    removePlanForNewlyUnavailableDay(menu, day, newOperatingHours);
                }

            }
        }

        cleanLeftoverTimeRanges(restaurant, newOperatingHours);
    }

    private void handleHeadModification(Map<DayOfWeek, TimeRange> newOperatingHours, Restaurant restaurant, DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevHoursOvernight = isOvernight(prevDayHours);
        Set<Menu> menus = restaurant.getMenus();
        Optional<TimeRange> bestTimeRangeTemplate = menus.stream()
                .flatMap(plan -> plan.getPlan().stream())
                .filter(p -> day.equals(p.getDayOfWeek()))
                .flatMap(plan -> plan.getTimeRanges().stream())
                .filter(timeRange -> !prevHoursOvernight || !LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                .min(pickEarliestTimeRange(dayHours));
        if (bestTimeRangeTemplate.isPresent()) {
            TimeRange bestTimeRange = bestTimeRangeTemplate.get();
            bestTimeRange.setStartTime(dayHours.getStartTime());
        }
    }

    private void handleTailModification(Map<DayOfWeek, TimeRange> newOperatingHours,
                                        Map<DayOfWeek, TimeRange> oldOperatingHours,
                                        Restaurant restaurant,
                                        DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);

        boolean currentOvernight = isOvernight(dayHours);
        if (currentOvernight) {
            handleTailModificationOvernight(newOperatingHours, oldOperatingHours, restaurant, day);
        } else {
            handleTailModification(newOperatingHours, restaurant, day);
        }
    }

    private void handleTailModificationOvernight(Map<DayOfWeek, TimeRange> newOperatingHours,
                                                 Map<DayOfWeek, TimeRange> oldOperatingHours,
                                                 Restaurant restaurant,
                                                 DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        TimeRange oldDayHours = oldOperatingHours.get(day);

        boolean wasOvernight = isOvernight(oldDayHours) && isOvernight(dayHours);
        Set<Menu> menus = restaurant.getMenus();

        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevHoursOvernight = isOvernight(prevDayHours);

        DayOfWeek nextDay = day.plus(1);
        Optional<Candidate> bestCandidateTemplate = menus.stream()
                .flatMap(menu -> menu.getPlan().stream()
                        .filter(p -> wasOvernight ? nextDay.equals(p.getDayOfWeek())
                                : day.equals(p.getDayOfWeek()))
                        .flatMap(p -> p.getTimeRanges().stream()
                                .filter(timeRange -> !prevHoursOvernight
                                        || !LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                                .map(tr -> new Candidate(menu, tr))))
                .max(Comparator.comparing(Candidate::range, pickLatestTimeRange(dayHours)));

        if (!wasOvernight && bestCandidateTemplate.isPresent()) {
            Candidate candidate = bestCandidateTemplate.get();
            TimeRange bestRange = candidate.range;
            Menu menuRef = candidate.menu;
            bestRange.setEndTime(LocalTime.MIDNIGHT);

            Optional<MenuPlan> menuPlanTemplate = getMenuPlanTemplate(menuRef, nextDay);
            TimeRange newTimeRange = new TimeRange(LocalTime.MIDNIGHT, dayHours.getEndTime());
            if (menuPlanTemplate.isPresent()) {
                MenuPlan menuPlan = menuPlanTemplate.get();
                menuPlan.getTimeRanges().add(newTimeRange);
            } else {
                MenuPlan newPlan = new MenuPlan();
                newPlan.setMenu(menuRef);
                newPlan.setDayOfWeek(nextDay);
                newPlan.setTimeRanges(Set.of(newTimeRange));
                menuRef.getPlan().add(newPlan);
            }
        } else if (wasOvernight && bestCandidateTemplate.isPresent()) {
            TimeRange bestTimeRange = bestCandidateTemplate.get().range;
            bestTimeRange.setEndTime(dayHours.getEndTime());
        }
    }

    private void handleTailModification(Map<DayOfWeek, TimeRange> newOperatingHours, Restaurant restaurant, DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        int newStartTimeMinutes = ClockPoint.start(dayHours.getStartTime()).asMinutes();

        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevHoursOvernight = isOvernight(prevDayHours);

        Set<Menu> menus = restaurant.getMenus();
        Optional<TimeRange> bestTimeRangeTemplate = menus.stream()
                .flatMap(plan -> plan.getPlan().stream())
                .filter(p -> day.equals(p.getDayOfWeek()))
                .flatMap(plan -> plan.getTimeRanges().stream())
                .filter(timeRange -> !prevHoursOvernight
                        || !LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                .max((e1, e2) -> {
                    int en1 = ClockPoint.end(e1.getEndTime()).asMinutes();
                    int en2 = ClockPoint.end(e2.getEndTime()).asMinutes();
                    int norm1 = en1 >= newStartTimeMinutes ? en1 : en1 + MINUTES_PER_DAY;
                    int norm2 = en2 >= newStartTimeMinutes ? en2 : en2 + MINUTES_PER_DAY;
                    return Integer.compare(norm1, norm2);
                });
        if (bestTimeRangeTemplate.isPresent()) {
            TimeRange bestTimeRange = bestTimeRangeTemplate.get();
            bestTimeRange.setEndTime(dayHours.getEndTime());
        }
    }

    private boolean wasClosedBefore(Map<DayOfWeek, TimeRange> oldOperatingHours,
                                    Map<DayOfWeek, TimeRange> newOperatingHours,
                                    DayOfWeek day) {
        return !oldOperatingHours.get(day).isAvailable() && newOperatingHours.get(day).isAvailable();
    }

    private boolean wasOpenedBefore(Map<DayOfWeek, TimeRange> oldOperatingHours,
                                    Map<DayOfWeek, TimeRange> newOperatingHours,
                                    DayOfWeek day) {
        return oldOperatingHours.get(day).isAvailable() && !newOperatingHours.get(day).isAvailable();
    }

    private void createPlanForNewlyAvailableDay(Menu menu,
                                                DayOfWeek day,
                                                Map<DayOfWeek, TimeRange> newOperatingHours) {
        if (!Boolean.TRUE.equals(menu.isStandard())) {
            return;
        }

        TimeRange operatingRanges = newOperatingHours.get(day);
        TimeRange prevHours = newOperatingHours.get(day.minus(1));
        boolean isPrevOvernight = isOvernight(prevHours);
        Optional<MenuPlan> templatePlan = menu.getPlan().stream()
                .filter(p -> day.equals(p.getDayOfWeek()))
                .findFirst();

        Set<TimeRange> rangesToPlan = Set.of(new TimeRange(operatingRanges.getStartTime(), operatingRanges.getEndTime()));
        if (operatingRanges.getStartTime().isAfter(operatingRanges.getEndTime())) {
            Set<TimeRange> splitRange = new HashSet<>();
            splitRange.add(new TimeRange(operatingRanges.getStartTime(), LocalTime.MIDNIGHT));
            rangesToPlan = splitRange;

            Optional<MenuPlan> nextDayPlanTemplate = menu.getPlan().stream()
                    .filter(p -> day.plus(1).equals(p.getDayOfWeek()))
                    .findFirst();
            Set<TimeRange> nextDayRange = Set.of(new TimeRange(LocalTime.MIDNIGHT, operatingRanges.getEndTime()));
            if (nextDayPlanTemplate.isEmpty()) {
                MenuPlan newPlan = new MenuPlan();
                newPlan.setMenu(menu);
                newPlan.setDayOfWeek(day.plus(1));
                newPlan.setTimeRanges(nextDayRange);
                menu.getPlan().add(newPlan);
            } else {
                MenuPlan nextDayPlan = nextDayPlanTemplate.get();
                nextDayPlan.getTimeRanges().addAll(nextDayRange);
                menu.getPlan().add(nextDayPlan);
            }
        }

        if (isPrevOvernight && templatePlan.isPresent()) {
            MenuPlan plan = templatePlan.orElseThrow();
            plan.getTimeRanges().addAll(rangesToPlan);
            menu.getPlan().removeIf(p -> day.equals(p.getDayOfWeek()));
            menu.getPlan().add(plan);
        } else if (!isPrevOvernight && templatePlan.isEmpty()) {
            MenuPlan newPlan = new MenuPlan();
            newPlan.setMenu(menu);
            newPlan.setDayOfWeek(day);
            newPlan.setTimeRanges(rangesToPlan);
            menu.getPlan().add(newPlan);
        }
    }

    private void removePlanForNewlyUnavailableDay(Menu menu,
                                                  DayOfWeek day,
                                                  Map<DayOfWeek, TimeRange> newOperatingHours) {
        Optional<MenuPlan> currentTemplatePlan = menu.getPlan().stream()
                .filter(p -> p.getDayOfWeek().equals(day))
                .findFirst();
        TimeRange prevHours = newOperatingHours.get(day.minus(1));
        boolean isPrevOvernight = isOvernight(prevHours);
        if (isPrevOvernight && currentTemplatePlan.isPresent()) {
            MenuPlan plan = currentTemplatePlan.get();

            Set<TimeRange> filteredRanges = plan.getTimeRanges().stream()
                    .filter(timeRange -> timeRange.getStartTime().equals(LocalTime.MIDNIGHT))
                    .collect(Collectors.toSet());

            MenuPlan newPlan = new MenuPlan();
            newPlan.setMenu(menu);
            newPlan.setDayOfWeek(day);
            newPlan.setTimeRanges(filteredRanges);
            menu.getPlan().removeIf(p -> day.equals(p.getDayOfWeek()));
            menu.getPlan().add(newPlan);
        }

        DayOfWeek nextDay = day.plus(1);
        Optional<MenuPlan> nextTemplatePlan = menu.getPlan().stream()
                .filter(p -> p.getDayOfWeek().equals(nextDay))
                .findFirst();
        TimeRange currentHours = newOperatingHours.get(day);
        boolean isCurrentOvernight = isOvernight(currentHours);
        if (isCurrentOvernight && nextTemplatePlan.isPresent()) {
            MenuPlan plan = nextTemplatePlan.get();

            Set<TimeRange> filteredRanges = plan.getTimeRanges().stream()
                    .filter(timeRange -> !timeRange.getStartTime().equals(LocalTime.MIDNIGHT))
                    .collect(Collectors.toSet());

            if (filteredRanges.isEmpty()) {
                menu.getPlan().removeIf(p -> nextDay.equals(p.getDayOfWeek()));
            } else {
                MenuPlan newPlan = new MenuPlan();
                newPlan.setMenu(menu);
                newPlan.setDayOfWeek(nextDay);
                newPlan.setTimeRanges(filteredRanges);
                menu.getPlan().removeIf(p -> nextDay.equals(p.getDayOfWeek()));
                menu.getPlan().add(newPlan);
            }
        }
    }

    private void cleanLeftoverTimeRanges(Restaurant restaurant, Map<DayOfWeek, TimeRange> newOperatingHours) {
        Set<Menu> menus = restaurant.getMenus();
        for (DayOfWeek day : newOperatingHours.keySet()) {
            TimeRange todayHours = newOperatingHours.get(day);
            TimeRange prevDayHours = newOperatingHours.get(day.minus(1));

            boolean prevDayOvernight = isOvernight(prevDayHours);

            for (Menu menu : menus) {
                for (MenuPlan plan : menu.getPlan()) {
                    if (!plan.getDayOfWeek().equals(day)) continue;

                    plan.getTimeRanges().removeIf(timeRange -> {
                        boolean intersectsToday = timeRange.intersect(todayHours) != null;
                        boolean intersectsFromYesterday = prevDayOvernight && timeRange.intersect(overnightPortionFrom(prevDayHours)) != null;

                        return !(intersectsToday || intersectsFromYesterday);
                    });
                }
            }
        }
    }


    private TimeRange overnightPortionFrom(TimeRange prevDayHours) {
        if (!isOvernight(prevDayHours)) return null;
        return new TimeRange(LocalTime.MIDNIGHT, prevDayHours.getEndTime());
    }

    private boolean isOvernight(TimeRange timeRange) {
        return ClockPoint.end(timeRange.getEndTime()).isBefore(ClockPoint.start(timeRange.getStartTime()));
    }

    private Optional<MenuPlan> getMenuPlanTemplate(Menu menu, DayOfWeek day) {
        return menu.getPlan().stream()
                .filter(plan ->
                        day.equals(plan.getDayOfWeek()))
                .findFirst();
    }

    /**
     * Returns the end of {@code tr} as “minutes since the start of the range”.
     * 22:00→01:00  ➜  1 800 min ➜  1 500 +  180 = 1 680 (i.e. 28 : 00)
     * 10:00→18:00  ➜  600  min (same-day)
     */
    private static int normalisedEnd(TimeRange tr) {
        int start = tr.getStartTime().toSecondOfDay() / 60;
        int end = tr.getEndTime().toSecondOfDay() / 60;

        if (end <= start) {
            end += MINUTES_PER_DAY;
        }
        return end;
    }

    private static Comparator<TimeRange> pickEarliestTimeRange(TimeRange newHours) {
        int newStart = ClockPoint.start(newHours.getStartTime()).asMinutes();
        return Comparator.comparingInt((TimeRange a) -> rankByStart(a, newStart))
                .thenComparingInt(a -> keyByStart(a, newStart));
    }

    private static Comparator<TimeRange> pickLatestTimeRange(TimeRange newHours) {
        int newEnd = ClockPoint.end(newHours.getEndTime()).asMinutes();
        return Comparator.comparingInt((TimeRange a) -> rankByEnd(a, newEnd))
                .thenComparingInt(a -> keyByEnd(a, newEnd));
    }

    static int rankByEnd(TimeRange tr, int newEnd) {
        int s = ClockPoint.start(tr.getStartTime()).asMinutes();
        int e = ClockPoint.end(tr.getEndTime()).asMinutes();
        return (s < newEnd && e >= newEnd) ? 0 : 1;
    }

    static int keyByEnd(TimeRange tr, int newEnd) {
        int e = ClockPoint.end(tr.getEndTime()).asMinutes();
        return rankByEnd(tr, newEnd) == 0 ? e
                : Math.abs(e - newEnd);
    }

    static int keyByStart(TimeRange tr, int newStart) {
        int s = ClockPoint.start(tr.getStartTime()).asMinutes();
        return rankByStart(tr, newStart) == 0 ? s
                : Math.abs(s - newStart);
    }

    static int rankByStart(TimeRange tr, int newStart) {
        int s = ClockPoint.start(tr.getStartTime()).asMinutes();
        int e = ClockPoint.end(tr.getEndTime()).asMinutes();
        return (s <= newStart && e > newStart) ? 0 : 1;
    }

    record Candidate(Menu menu, TimeRange range) {
    }

}
