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

        // 3) placeholders for which MenuPlans to extend at head/tail
        Map<DayOfWeek, MenuPlan> headExtendTarget = new HashMap<>();
        Map<DayOfWeek, MenuPlan> tailExtendTarget = new HashMap<>();

        // 4) figure out, for each day, whether the start moved earlier (head) or the end moved later (tail)
        UUID alreadyProcessedId = null;
        for (DayOfWeek day : newOperatingHours.keySet()) {
            TimeRange oldHours = oldOperatingHours.get(day);
            TimeRange newHours = newOperatingHours.get(day);

            boolean wasClosedBefore = wasClosedBefore(oldOperatingHours, newOperatingHours, day);
            TimeRange prevDayHours = newOperatingHours.get(day.minus(1));
            boolean isCurrentDayOvernight = isOvernight(newHours);
            boolean isPrevDayOvernight = isOvernight(prevDayHours);
            if (wasClosedBefore || (!oldHours.isAvailable() || !newHours.isAvailable()) && !isCurrentDayOvernight) {
                continue;
            }

            ClockPoint oldStart = ClockPoint.start(oldHours.getStartTime());
            ClockPoint oldEndRaw = ClockPoint.end(oldHours.getEndTime());
            ClockPoint newStart = ClockPoint.start(newHours.getStartTime());
            ClockPoint newEndRaw = ClockPoint.end(newHours.getEndTime());

            int oldNormEnd = oldStart.isBefore(oldEndRaw) ? oldEndRaw.asMinutes() : oldEndRaw.asMinutes() + MINUTES_PER_DAY;
            int newNormEnd = newStart.isBefore(newEndRaw) ? newEndRaw.asMinutes() : newEndRaw.asMinutes() + MINUTES_PER_DAY;

            // if new opening is earlier, find the plan block nearest the new start
            if (newStart.isBefore(oldStart)) {
                MenuPlan bestHead = restaurant.getMenus().stream()
                        .flatMap(menu -> menu.getPlan().stream())
                        .filter(plan -> plan.getDayOfWeek() == day)
                        .map(plan -> new AbstractMap.SimpleEntry<>(plan, headDistance(plan, newHours, newStart.asMinutes())))
                        .filter(entry -> entry.getValue() != null)
                        .min(Comparator.comparingInt(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .orElse(null);
                headExtendTarget.put(day, bestHead);
            }

            // if new closing is later, find the plan block that can be extended at the tail
            if (newNormEnd > oldNormEnd) {
                DayOfWeek nextDay = day.plus(1);
                Optional<AbstractMap.SimpleEntry<MenuPlan, TimeRange>> best =
                        restaurant.getMenus().stream()
                                .flatMap(menu -> menu.getPlan().stream())
                                .filter(p -> p.getDayOfWeek() == day ||
                                        (isPrevDayOvernight && !isCurrentDayOvernight && p.getDayOfWeek() == nextDay)
                                )
                                .flatMap(plan -> plan.getTimeRanges().stream()
                                        .filter(tr -> {
                                            boolean intersects = tr.intersect(newHours) != null || (isCurrentDayOvernight
                                                    && plan.getDayOfWeek() == nextDay
                                                    && tr.getStartTime().isBefore(newHours.getEndTime()));

                                            boolean isStartMidnight = tr.getStartTime().equals(LocalTime.MIDNIGHT);
                                            boolean isDayMatch = plan.getDayOfWeek() == day;

                                            if (isStartMidnight && isDayMatch && isPrevDayOvernight) {
                                                return false;
                                            }

                                            return intersects;
                                        })
                                        .map(tr -> new AbstractMap.SimpleEntry<>(plan, tr))
                                )
                                .filter(e -> {
                                    int en = ClockPoint.end(e.getValue().getEndTime()).asMinutes();
                                    int normEn = en >= newStart.asMinutes() ? en : en + MINUTES_PER_DAY;
                                    return normEn <= newNormEnd;
                                })
                                .max((e1, e2) -> {
                                    int en1 = ClockPoint.end(e1.getValue().getEndTime()).asMinutes();
                                    int en2 = ClockPoint.end(e2.getValue().getEndTime()).asMinutes();
                                    int norm1 = en1 >= newStart.asMinutes() ? en1 : en1 + MINUTES_PER_DAY;
                                    int norm2 = en2 >= newStart.asMinutes() ? en2 : en2 + MINUTES_PER_DAY;
                                    return Integer.compare(norm1, norm2);
                                });

                // if best candidate is on the same day, schedule it for tail extension
                if (best.isPresent()) {
                    MenuPlan plan = best.get().getKey();
                    TimeRange rng = best.get().getValue();
                    if (plan.getDayOfWeek() == day) {
                        tailExtendTarget.put(day, plan);
                    } else {
                        plan.getTimeRanges().removeIf(tr -> tr.equals(rng));
                        rng.setEndTime(newHours.getEndTime());
                        plan.getTimeRanges().add(rng);
                        alreadyProcessedId = plan.getId();
                    }
                }
            }
        }

        // 5) now iterate every Menu and each of its MenuPlans to trim, remove, or extend time ranges
        for (Menu menu : restaurant.getMenus()) {
            for (MenuPlan plan : new HashSet<>(menu.getPlan())) {
                if (alreadyProcessedId == plan.getId()) {
                    continue;
                }
                DayOfWeek day = plan.getDayOfWeek();
                TimeRange oldHours = oldOperatingHours.get(day);
                TimeRange newHours = newOperatingHours.get(day);

                // A) handle days that are no longer available: try to spill back into previous day if overnight
                if (!newHours.isAvailable()) {
                    TimeRange prevOp = newOperatingHours.get(day.minus(1));
                    if (prevOp != null && prevOp.isAvailable()) {
                        Set<TimeRange> spill = plan.getTimeRanges().stream()
                                .map(tr -> tr.intersect(prevOp))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                        if (!spill.isEmpty()) {
                            plan.getTimeRanges().clear();
                            plan.getTimeRanges().addAll(spill);
                            continue;
                        }
                    }
                }

                // B) if still closed and no overnight spill, drop the plan or filter overflow bits
                if (!newHours.isAvailable()) {
                    Set<TimeRange> overflow = plan.getTimeRanges().stream()
                            .filter(tr -> isOverflow(tr, oldHours))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    if (overflow.isEmpty()) {
                        menu.getPlan().remove(plan);
                    } else {
                        plan.getTimeRanges().clear();
                        plan.getTimeRanges().addAll(overflow);
                    }
                    continue;
                }

                ClockPoint newOperatingStart = ClockPoint.start(newHours.getStartTime());
                ClockPoint newOperatingEnd = ClockPoint.end(newHours.getEndTime());
                ClockPoint oldOperatingStart = ClockPoint.start(oldHours.getStartTime());
                ClockPoint oldOperatingEnd = ClockPoint.end(oldHours.getEndTime());
                boolean isOldOvernight = oldOperatingEnd.isBefore(oldOperatingStart);
                boolean isNewOvernight = newOperatingEnd.isBefore(newOperatingStart);

                if (isOldOvernight && !isNewOvernight) {
                    Optional<MenuPlan> planToRemoveTemplate = menu.getPlan().stream().filter(p ->
                            day.plus(1).equals(p.getDayOfWeek())
                    ).findFirst();
                    if (planToRemoveTemplate.isPresent()) {
                        Set<TimeRange> timeRanges = planToRemoveTemplate.get().getTimeRanges();
                        Set<TimeRange> filteredRanges = timeRanges.stream()
                                .filter(timeRange -> !timeRange.getStartTime().equals(LocalTime.MIDNIGHT))
                                .collect(Collectors.toSet());
                        if (filteredRanges.isEmpty()) {
                            menu.getPlan().removeIf(p -> day.plus(1).equals(p.getDayOfWeek()));
                        } else {
                            MenuPlan newPlan = planToRemoveTemplate.get();
                            newPlan.setTimeRanges(filteredRanges);
                            menu.getPlan().removeIf(p -> day.plus(1).equals(p.getDayOfWeek()));
                            menu.getPlan().add(newPlan);
                        }
                    }
                }

                // D) otherwise trim each TimeRange to fit within the new hours
                Set<TimeRange> overnightLeftover = new HashSet<>();
                Set<TimeRange> trimmed = plan.getTimeRanges().stream()
                        .map(tr -> {
                            TimeRange prevOp = newOperatingHours.get(day.minus(1));
                            boolean prevOvernight = isOvernight(prevOp);
                            if (prevOvernight && LocalTime.MIDNIGHT.equals(tr.getStartTime())) {
                                return tr;
                            }
                            return tr.intersect(newHours);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                if (trimmed.isEmpty()) {
                    menu.getPlan().remove(plan);
                    continue;
                }

                // E) check if we should extend head or tail for this plan
                int oldStartT = ClockPoint.start(oldHours.getStartTime()).asMinutes();
                int oldEndRawT = ClockPoint.end(oldHours.getEndTime()).asMinutes();
                int newStartT = ClockPoint.start(newHours.getStartTime()).asMinutes();
                int newEndRawT = ClockPoint.end(newHours.getEndTime()).asMinutes();
                int oldNormEndT = oldEndRawT > oldStartT ? oldEndRawT : oldEndRawT + MINUTES_PER_DAY;
                int newNormEndT = newEndRawT > newStartT ? newEndRawT : newEndRawT + MINUTES_PER_DAY;
                boolean extendHead = newStartT < oldStartT;
                boolean extendTail = newNormEndT > oldNormEndT;

                // F) if this plan is the chosen head target, extend its start
                if (extendHead && plan.equals(headExtendTarget.get(day))) {
                    trimmed.stream()
                            .min(Comparator.comparingInt(tr -> {
                                int st = ClockPoint.start(tr.getStartTime()).asMinutes();
                                int norm = st >= newStartT ? st : st + MINUTES_PER_DAY;
                                return norm - newStartT;
                            }))
                            .ifPresent(best -> best.setStartTime(newHours.getStartTime()));
                }

                // G) if this plan is the chosen tail target, extend its end (possibly spilling to next day)
                if (extendTail && plan.equals(tailExtendTarget.get(day))) {
                    // pick the segment whose end is closest to the new closing
                    Comparator<TimeRange> endDist = Comparator.comparingInt(tr -> {
                        int en = ClockPoint.end(tr.getEndTime()).asMinutes();
                        int norm = en >= newStartT ? en : en + MINUTES_PER_DAY;
                        return newNormEndT - norm;
                    });

                    TimeRange prevOperatingHours = newOperatingHours.get(day.minus(1));
                    boolean isPrevOvernight = ClockPoint.end(prevOperatingHours.getEndTime())
                            .isBefore(ClockPoint.start(prevOperatingHours.getStartTime()));
                    if (isPrevOvernight) {
                        overnightLeftover = trimmed.stream()
                                .filter(timeRange -> LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                                .collect(Collectors.toSet());
                        trimmed = trimmed.stream()
                                .filter(timeRange -> !LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                                .collect(Collectors.toSet());
                    }

                    trimmed.stream()
                            .min(endDist)
                            .filter(timeRange -> !LocalTime.MIDNIGHT.equals(timeRange.getStartTime()))
                            .ifPresent(toGrow -> {
                                boolean isOvernightExtend = newNormEndT - MINUTES_PER_DAY > 0;
                                // If it's a same-day extension OR already a midnight-start spill, just extend straight through
                                if (!isOvernightExtend) {
                                    toGrow.setEndTime(newHours.getEndTime());
                                } else {
                                    // true overnight extension → cap at midnight today, and spill the rest into next day's plan
                                    toGrow.setEndTime(LocalTime.MIDNIGHT);

                                    DayOfWeek next = day.plus(1);
                                    // find or create the MenuPlan for the next day
                                    MenuPlan nextPlan = menu.getPlan().stream()
                                            .filter(p -> p.getDayOfWeek() == next)
                                            .findFirst()
                                            .orElseGet(() -> {
                                                MenuPlan np = new MenuPlan();
                                                np.setMenu(menu);
                                                np.setDayOfWeek(next);
                                                np.setTimeRanges(new LinkedHashSet<>());
                                                menu.getPlan().add(np);
                                                return np;
                                            });

                                    // add the overflow segment into nextPlan
                                    TimeRange overflow = new TimeRange(LocalTime.MIDNIGHT, newHours.getEndTime());
                                    nextPlan.getTimeRanges().removeIf(timeRange ->
                                            LocalTime.MIDNIGHT.equals(timeRange.getStartTime()));
                                    nextPlan.getTimeRanges().add(overflow);
                                }
                            });
                }


                // H) replace original plan’s ranges with the trimmed/extended set
                plan.getTimeRanges().clear();
                plan.getTimeRanges().addAll(trimmed);
                plan.getTimeRanges().addAll(overnightLeftover);
            }
        }

        // 6) for each day that just opened up/closed down, create fresh plans off a template/remove plans
        for (Menu menu : restaurant.getMenus()) {
            for (Map.Entry<DayOfWeek, TimeRange> e : newOperatingHours.entrySet()) {
                DayOfWeek day = e.getKey();

                // newly available days → seed a plan
                if (wasClosedBefore(oldOperatingHours, newOperatingHours, day)) {
                    createPlanForNewlyAvailableDay(menu, day, newOperatingHours);
                }
                // newly unavailable days → redistribute or chop plans
                if (wasOpenedBefore(oldOperatingHours, newOperatingHours, day)) {
                    removePlanForNewlyUnavailableDay(menu, day, newOperatingHours);
                }
            }
        }
    }

    /**
     * Helper for picking the plan segment closest to the new opening time.
     */
    private Integer headDistance(MenuPlan p, TimeRange newHours, int newStart) {
        List<TimeRange> trimmedRanges = p.getTimeRanges().stream()
                .map(timeRange -> {
                    TimeRange ov = timeRange.intersect(newHours);
                    return (ov != null ? ov.withAvailable(timeRange.isAvailable()) : null);
                })
                .filter(Objects::nonNull)
                .toList();
        if (trimmedRanges.isEmpty()) return null;

        return trimmedRanges.stream()
                .mapToInt(timeRange -> {
                    int s = ClockPoint.start(timeRange.getStartTime()).asMinutes();
                    int normS = (s >= newStart ? s : s + MINUTES_PER_DAY);
                    return normS - newStart;
                })
                .min().orElseThrow();
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
        boolean isPrevOvernight = ClockPoint.end(prevHours.getEndTime()).isBefore(ClockPoint.start(prevHours.getStartTime()));
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
        boolean isPrevOvernight = ClockPoint.end(prevHours.getEndTime()).isBefore(ClockPoint.start(prevHours.getStartTime()));
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
        boolean isCurrentOvernight = ClockPoint.end(currentHours.getEndTime()).isBefore(ClockPoint.start(currentHours.getStartTime()));
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

    /**
     * Checks if a TimeRange lies fully outside the old operating hours.
     */
    private boolean isOverflow(TimeRange tr, TimeRange oldHours) {
        int s = ClockPoint.start(tr.getStartTime()).asMinutes();
        int e = ClockPoint.end(tr.getEndTime()).minutes();
        int oldStart = ClockPoint.start(oldHours.getStartTime()).asMinutes();
        int oldEnd = ClockPoint.end(oldHours.getEndTime()).asMinutes();
        int oldNormEnd = oldEnd > oldStart ? oldEnd : oldEnd + MINUTES_PER_DAY;
        int normStart = (e > s ? s : s + MINUTES_PER_DAY);
        int normEnd = (e > s ? e : e + MINUTES_PER_DAY);
        return normEnd <= oldStart || normStart >= oldNormEnd;
    }

    private boolean isOvernight(TimeRange timeRange) {
        return ClockPoint.end(timeRange.getEndTime()).isBefore(ClockPoint.start(timeRange.getStartTime()));
    }
}
