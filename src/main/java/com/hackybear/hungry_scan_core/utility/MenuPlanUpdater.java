package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.record.ClockPoint;
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
    private Map<DayOfWeek, TimeRange> newOperatingHours;
    private Map<DayOfWeek, TimeRange> oldOperatingHours;

    public void updateMenusPlans(Restaurant restaurant, RestaurantDTO restaurantDTO) {
        Settings settings = restaurant.getSettings();
        SettingsDTO settingsDTO = restaurantDTO.settings();

        this.oldOperatingHours = settings.getOperatingHours();
        this.newOperatingHours = settingsDTO.operatingHours();

        if (Objects.equals(oldOperatingHours, newOperatingHours)) {
            return;
        }

        trimMenuPlansTimeRanges(restaurant);
        handleOpenedAndClosedDays(restaurant);
        cleanLeftoverTimeRanges(restaurant);
    }

    private void trimMenuPlansTimeRanges(Restaurant restaurant) {
        for (DayOfWeek day : newOperatingHours.keySet()) {

            if (shouldNotTrim(day)) {
                continue;
            }

            TimeRange newDayHours = newOperatingHours.get(day);
            TimeRange oldDayHours = oldOperatingHours.get(day);
            ClockPoint newStart = ClockPoint.start(newDayHours.getStartTime());
            ClockPoint oldStart = ClockPoint.start(oldDayHours.getStartTime());

            if (shouldModifyHead(newStart, oldStart)) {
                handleHeadModification(restaurant, day);
            }

            if (shouldModifyTail(newDayHours, oldDayHours)) {
                handleTailModification(restaurant, day);
            }

        }
    }

    private void handleOpenedAndClosedDays(Restaurant restaurant) {
        for (Menu menu : restaurant.getMenus()) {
            for (Map.Entry<DayOfWeek, TimeRange> e : newOperatingHours.entrySet()) {
                DayOfWeek day = e.getKey();

                if (wasClosedBefore(day)) {
                    createPlanForNewlyAvailableDay(menu, day);
                } else if (wasOpenedBefore(day)) {
                    removePlanForNewlyUnavailableDay(menu, day);
                }

            }
        }
    }

    private void handleHeadModification(Restaurant restaurant, DayOfWeek day) {
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

    private void handleTailModification(Restaurant restaurant,
                                        DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);

        boolean currentOvernight = isOvernight(dayHours);
        if (currentOvernight) {
            handleTailModificationOvernight(restaurant, day);
        } else {
            handleStandardTailModification(restaurant, day);
        }
    }

    private void handleTailModificationOvernight(Restaurant restaurant,
                                                 DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        TimeRange oldDayHours = oldOperatingHours.get(day);

        boolean wasOvernight = isOvernight(oldDayHours) && isOvernight(dayHours);
        DayOfWeek nextDay = day.plus(1);

        Optional<Candidate> bestCandidateTemplate = pickBestForTailOvernight(restaurant, day);
        if (!wasOvernight && bestCandidateTemplate.isPresent()) {
            Candidate candidate = bestCandidateTemplate.get();
            TimeRange bestRange = candidate.range;
            Menu menuRef = candidate.menuPlan.getMenu();
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
                Set<TimeRange> newTimeRanges = new HashSet<>();
                newTimeRanges.add(newTimeRange);
                newPlan.setTimeRanges(newTimeRanges);
                menuRef.getPlan().add(newPlan);
            }
        } else if (wasOvernight && bestCandidateTemplate.isPresent()) {
            TimeRange bestTimeRange = bestCandidateTemplate.get().range;
            bestTimeRange.setEndTime(dayHours.getEndTime());
        }
    }

    private Optional<Candidate> pickBestForTailOvernight(Restaurant restaurant, DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        TimeRange oldDayHours = oldOperatingHours.get(day);

        boolean wasOvernight = isOvernight(oldDayHours) && isOvernight(dayHours);

        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevHoursOvernight = isOvernight(prevDayHours);

        ClockPoint newStart = ClockPoint.start(dayHours.getStartTime());
        DayOfWeek nextDay = day.plus(1);

        Set<Menu> menus = restaurant.getMenus();
        return menus.stream()
                .flatMap(menu -> menu.getPlan().stream()
                        .filter(p -> wasOvernight ? nextDay.equals(p.getDayOfWeek())
                                : day.equals(p.getDayOfWeek()))
                        .flatMap(p -> p.getTimeRanges().stream()
                                .map(tr -> new Candidate(p, tr)))
                        .filter(candidate -> {
                            if (Objects.isNull(candidate.range.intersect(dayHours))) {
                                return false;
                            }
                            if (prevHoursOvernight && day.equals(candidate.menuPlan.getDayOfWeek())) {
                                return Objects.isNull(candidate.range.intersect(overnightPortionFrom(prevDayHours)));
                            } else if (nextDay.equals(candidate.menuPlan.getDayOfWeek())) {
                                return Objects.nonNull(candidate.range.intersect(overnightPortionFrom(dayHours)));
                            }
                            return true;
                        }))
                .max((e1, e2) -> {
                    TimeRange firstEnd = e1.range;
                    TimeRange secondEnd = e2.range;
                    int en1 = ClockPoint.end(firstEnd.getEndTime()).asMinutes();
                    int en2 = ClockPoint.end(secondEnd.getEndTime()).asMinutes();
                    int norm1 = en1 >= newStart.asMinutes() ? en1 : en1 + MINUTES_PER_DAY;
                    int norm2 = en2 >= newStart.asMinutes() ? en2 : en2 + MINUTES_PER_DAY;
                    return Integer.compare(norm1, norm2);
                });
    }

    private void handleStandardTailModification(Restaurant restaurant, DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        Optional<TimeRange> bestTimeRangeTemplate = pickBestForStandardTailModification(restaurant, day);
        if (bestTimeRangeTemplate.isPresent()) {
            TimeRange bestTimeRange = bestTimeRangeTemplate.get();
            bestTimeRange.setEndTime(dayHours.getEndTime());
        }
    }

    private Optional<TimeRange> pickBestForStandardTailModification(Restaurant restaurant, DayOfWeek day) {
        TimeRange dayHours = newOperatingHours.get(day);
        int newStartTimeMinutes = ClockPoint.start(dayHours.getStartTime()).asMinutes();

        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevHoursOvernight = isOvernight(prevDayHours);

        Set<Menu> menus = restaurant.getMenus();
        return menus.stream()
                .flatMap(plan -> plan.getPlan().stream())
                .filter(p -> day.equals(p.getDayOfWeek()))
                .flatMap(plan -> plan.getTimeRanges().stream())
                .filter(timeRange -> {
                    if (Objects.isNull(timeRange.intersect(dayHours))) {
                        return false;
                    }
                    return !prevHoursOvernight
                            || !Objects.nonNull(timeRange.intersect(overnightPortionFrom(prevDayHours)));
                })
                .max((e1, e2) -> {
                    int en1 = ClockPoint.end(e1.getEndTime()).asMinutes();
                    int en2 = ClockPoint.end(e2.getEndTime()).asMinutes();
                    int norm1 = en1 >= newStartTimeMinutes ? en1 : en1 + MINUTES_PER_DAY;
                    int norm2 = en2 >= newStartTimeMinutes ? en2 : en2 + MINUTES_PER_DAY;
                    return Integer.compare(norm1, norm2);
                });
    }

    private boolean wasClosedBefore(DayOfWeek day) {
        return !oldOperatingHours.get(day).isAvailable() && newOperatingHours.get(day).isAvailable();
    }

    private boolean wasOpenedBefore(DayOfWeek day) {
        return oldOperatingHours.get(day).isAvailable() && !newOperatingHours.get(day).isAvailable();
    }

    private boolean shouldModifyHead(ClockPoint newStart, ClockPoint oldStart) {
        boolean headExtension = newStart.isBefore(oldStart);
        boolean headShrink = oldStart.isBefore(newStart);
        return headExtension || headShrink;
    }

    private boolean shouldModifyTail(TimeRange newDayHours, TimeRange oldDayHours) {
        boolean tailExtension = normalisedEnd(newDayHours) > normalisedEnd(oldDayHours);
        boolean tailShrink = normalisedEnd(newDayHours) < normalisedEnd(oldDayHours);
        return tailExtension || tailShrink;
    }

    private boolean shouldNotTrim(DayOfWeek day) {
        boolean wasOpenedBefore = wasOpenedBefore(day);
        boolean wasClosedBefore = wasClosedBefore(day);
        return wasOpenedBefore || wasClosedBefore;
    }

    private void createPlanForNewlyAvailableDay(Menu menu, DayOfWeek day) {
        if (Boolean.FALSE.equals(menu.isStandard())) {
            return;
        }

        TimeRange operatingRanges = newOperatingHours.get(day);
        TimeRange prevHours = newOperatingHours.get(day.minus(1));
        boolean isPrevOvernight = isOvernight(prevHours);
        Optional<MenuPlan> templatePlan = menu.getPlan().stream()
                .filter(p -> day.equals(p.getDayOfWeek()))
                .findFirst();

        Set<TimeRange> rangesToPlan = new HashSet<>();
        rangesToPlan.add(new TimeRange(operatingRanges.getStartTime(), operatingRanges.getEndTime()));
        if (isOvernight(operatingRanges)) {
            Set<TimeRange> splitRange = new HashSet<>();
            splitRange.add(new TimeRange(operatingRanges.getStartTime(), LocalTime.MIDNIGHT));
            rangesToPlan = splitRange;

            Optional<MenuPlan> nextDayPlanTemplate = menu.getPlan().stream()
                    .filter(p -> day.plus(1).equals(p.getDayOfWeek()))
                    .findFirst();
            Set<TimeRange> nextDayRange = new HashSet<>();
            nextDayRange.add(new TimeRange(LocalTime.MIDNIGHT, operatingRanges.getEndTime()));
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
        } else {
            MenuPlan newPlan = new MenuPlan();
            newPlan.setMenu(menu);
            newPlan.setDayOfWeek(day);
            newPlan.setTimeRanges(rangesToPlan);
            menu.getPlan().add(newPlan);
        }
    }

    private void removePlanForNewlyUnavailableDay(Menu menu, DayOfWeek day) {
        Optional<MenuPlan> currentTemplatePlan = menu.getPlan().stream()
                .filter(p -> p.getDayOfWeek().equals(day))
                .findFirst();
        TimeRange prevHours = newOperatingHours.get(day.minus(1));
        boolean isPrevOvernight = isOvernight(prevHours);
        if (isPrevOvernight && currentTemplatePlan.isPresent()) {
            MenuPlan plan = currentTemplatePlan.get();

            TimeRange prevOvernightPortion = overnightPortionFrom(prevHours);

            Set<TimeRange> filteredRanges = plan.getTimeRanges().stream()
                    .filter(timeRange -> Objects.nonNull(timeRange.intersect(prevOvernightPortion)))
                    .collect(Collectors.toSet());

            MenuPlan newPlan = new MenuPlan();
            newPlan.setMenu(menu);
            newPlan.setDayOfWeek(day);
            newPlan.setTimeRanges(filteredRanges);
            menu.getPlan().removeIf(p -> day.equals(p.getDayOfWeek()));
            menu.getPlan().add(newPlan);
        } else if (currentTemplatePlan.isPresent()) {
            menu.getPlan().removeIf(p -> day.equals(p.getDayOfWeek()));
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

    private void cleanLeftoverTimeRanges(Restaurant restaurant) {
        for (Menu menu : restaurant.getMenus()) {
            Iterator<MenuPlan> plansIterator = menu.getPlan().iterator();
            while (plansIterator.hasNext()) {
                MenuPlan plan = plansIterator.next();
                DayOfWeek day = plan.getDayOfWeek();
                removeTimeRanges(plan, day);
                if (plan.getTimeRanges().isEmpty()) {
                    plansIterator.remove();
                }
            }
        }
    }

    private void removeTimeRanges(MenuPlan plan, DayOfWeek day) {
        TimeRange todayHours = newOperatingHours.get(day);
        TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
        boolean prevOvernight = isOvernight(prevDayHours);
        plan.getTimeRanges().removeIf(tr -> {
            boolean intersectsToday = todayHours.isAvailable() &&
                    Objects.nonNull(tr.intersect(todayHours));
            boolean intersectsFromYesterday = prevOvernight &&
                    Objects.nonNull(tr.intersect(overnightPortionFrom(prevDayHours)));
            return !(intersectsToday || intersectsFromYesterday);
        });
    }

    private TimeRange overnightPortionFrom(TimeRange prevDayHours) {
        if (!isOvernight(prevDayHours)) return null;
        return new TimeRange(LocalTime.MIDNIGHT, prevDayHours.getEndTime());
    }

    private boolean isOvernight(TimeRange timeRange) {
        return ClockPoint.end(timeRange.getEndTime()).isBefore(ClockPoint.start(timeRange.getStartTime())) ||
                ClockPoint.end(timeRange.getEndTime()).equals(ClockPoint.start(timeRange.getStartTime()));
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

    static int rankByStart(TimeRange tr, int newStart) {
        int start = ClockPoint.start(tr.getStartTime()).asMinutes();
        int end = ClockPoint.end(tr.getEndTime()).asMinutes();
        return (start <= newStart && end > newStart) ? 0 : 1;
    }

    static int keyByStart(TimeRange tr, int newStart) {
        int start = ClockPoint.start(tr.getStartTime()).asMinutes();
        return rankByStart(tr, newStart) == 0 ? start
                : Math.abs(start - newStart);
    }

    record Candidate(MenuPlan menuPlan, TimeRange range) {
    }

}
