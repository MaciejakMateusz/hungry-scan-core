package com.hackybear.hungry_scan_core.validator;

import com.hackybear.hungry_scan_core.dto.MenuPlanDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MenuPlanValidator {

    private final SettingsRepository settingsRepository;
    private final ExceptionHelper exceptionHelper;

    private static final int MINUTES_PER_DAY = 24 * 60;

    public void validateMenusPlans(List<MenuSimpleDTO> menuDTOs,
                                   Long restaurantId) throws LocalizedException {
        validateScheduleEmptiness(menuDTOs);

        Settings settings = settingsRepository.findByRestaurantId(restaurantId);
        Map<DayOfWeek, TimeRange> operatingHours = settings.getOperatingHours();

        Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay = buildScheduleEntries(menuDTOs);
        validatePlans(scheduleByDay, operatingHours);
    }

    private void validateScheduleEmptiness(List<MenuSimpleDTO> menuDTOs)
            throws LocalizedException {
        boolean allEmpty = menuDTOs.stream()
                .allMatch(dto -> dto.plan().isEmpty());
        if (allEmpty) {
            throw new LocalizedException("error.menuService.scheduleIncomplete");
        }
    }

    private Map<DayOfWeek, List<ScheduleEntry>> buildScheduleEntries(
            List<MenuSimpleDTO> menuDTOs) {
        var map = new EnumMap<DayOfWeek, List<ScheduleEntry>>(DayOfWeek.class);
        for (MenuSimpleDTO menu : menuDTOs) {
            for (MenuPlanDTO plan : menu.plan()) {
                DayOfWeek day = plan.dayOfWeek();
                for (TimeRange tr : plan.timeRanges()) {
                    map.computeIfAbsent(day, d -> new ArrayList<>())
                            .add(new ScheduleEntry(menu, tr));
                }
            }
        }
        return map;
    }

    private void validatePlans(Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay,
                               Map<DayOfWeek, TimeRange> operatingHours)
            throws LocalizedException {

        Map<DayOfWeek, List<NormRange>> required = buildRequiredSegments(operatingHours);
        Map<DayOfWeek, List<NormRange>> planned = buildPlannedSegments(scheduleByDay);

        for (DayOfWeek day : DayOfWeek.values()) {
            List<NormRange> requiredRanges = required.get(day);
            if (requiredRanges == null || requiredRanges.isEmpty()) {
                continue;
            }

            List<NormRange> plans = planned.get(day);
            if (Objects.isNull(plans) || plans.isEmpty()) {
                exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
            }

            for (NormRange plan : plans) {
                int covered = 0;
                for (NormRange r : requiredRanges) {
                    int s = Math.max(plan.start, r.start);
                    int e = Math.min(plan.end, r.end);
                    if (e > s) covered += (e - s);
                }
                int planLen = plan.end - plan.start;
                if (planLen <= 0) {
                    exceptionHelper.throwLocalizedMessage("error.menuService.invalidRange");
                }
                if (covered < planLen) {
                    exceptionHelper.throwLocalizedMessage("error.menuService.scheduleNotWithinOpeningHours");
                }
            }

            requiredRanges.sort(Comparator.comparingInt(r -> r.start));
            plans.sort(Comparator.comparingInt(r -> r.start));

            for (NormRange r : requiredRanges) {
                List<NormRange> slice = new ArrayList<>();
                for (NormRange p : plans) {
                    if (p.end > r.start && p.start < r.end) {
                        int s = Math.max(p.start, r.start);
                        int e = Math.min(p.end, r.end);
                        slice.add(new NormRange(s, e));
                    }
                }
                if (slice.isEmpty()) {
                    exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
                }
                slice.sort(Comparator.comparingInt(x -> x.start));

                int cursor = r.start;
                for (NormRange seg : slice) {
                    if (seg.start > cursor) {
                        exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
                    }
                    if (seg.start < cursor) {
                        exceptionHelper.throwLocalizedMessage("error.menuService.schedulesCollide");
                    }
                    cursor = seg.end;
                }
                if (cursor < r.end) {
                    exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
                }
            }
        }
    }

    private void addRange(Map<DayOfWeek, List<NormRange>> byDay,
                          DayOfWeek day, int start, int end) {
        if (end == 0) end = MINUTES_PER_DAY;

        if (end > start) {
            byDay.computeIfAbsent(day, d -> new ArrayList<>())
                    .add(new NormRange(start, end));
        } else {
            byDay.computeIfAbsent(day, d -> new ArrayList<>())
                    .add(new NormRange(start, MINUTES_PER_DAY));
            DayOfWeek next = day.plus(1);
            byDay.computeIfAbsent(next, d -> new ArrayList<>())
                    .add(new NormRange(0, end));
        }
    }

    private Map<DayOfWeek, List<NormRange>> buildRequiredSegments(
            Map<DayOfWeek, TimeRange> operatingHours) {
        var map = new EnumMap<DayOfWeek, List<NormRange>>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            TimeRange op = operatingHours.get(day);
            if (op == null || !op.isAvailable()) continue;

            int s = op.getStartTime().getHour() * 60 + op.getStartTime().getMinute();
            int e = op.getEndTime().getHour() * 60 + op.getEndTime().getMinute();

            addRange(map, day, s, e);
        }
        return map;
    }

    private Map<DayOfWeek, List<NormRange>> buildPlannedSegments(
            Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay) {
        var map = new EnumMap<DayOfWeek, List<NormRange>>(DayOfWeek.class);

        for (var ent : scheduleByDay.entrySet()) {
            DayOfWeek day = ent.getKey();

            for (ScheduleEntry se : ent.getValue()) {
                TimeRange tr = se.range;
                int s = tr.getStartTime().getHour() * 60 + tr.getStartTime().getMinute();
                int e = tr.getEndTime().getHour() * 60 + tr.getEndTime().getMinute();

                addRange(map, day, s, e);
            }
        }
        return map;
    }

    private void addReq(Map<DayOfWeek, List<NormRange>> m,
                        DayOfWeek d, int s, int e) {
        if (e <= s) return;
        m.computeIfAbsent(d, x -> new ArrayList<>()).add(new NormRange(s, e));
    }

    private void addPlan(Map<DayOfWeek, List<NormRange>> m,
                         DayOfWeek d, int s, int e) {
        if (e <= s) return;
        m.computeIfAbsent(d, x -> new ArrayList<>()).add(new NormRange(s, e));
    }

    private static final class NormRange {
        final int start, end;

        NormRange(int s, int e) {
            this.start = s;
            this.end = e;
        }
    }

    private static final class ScheduleEntry {
        final MenuSimpleDTO menu;
        final TimeRange range;

        ScheduleEntry(MenuSimpleDTO m, TimeRange r) {
            this.menu = m;
            this.range = r;
        }
    }
}
