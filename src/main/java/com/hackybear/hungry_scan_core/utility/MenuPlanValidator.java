package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuPlanDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
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
            List<NormRange> reqs = required.get(day);
            if (reqs == null || reqs.isEmpty()) {
                continue;
            }

            List<NormRange> plans = planned.get(day);
            if (Objects.isNull(plans) || plans.isEmpty()) {
                exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
            }

            for (NormRange p : plans) {
                boolean ok = reqs.stream()
                        .anyMatch(r -> p.start >= r.start && p.end <= r.end);
                if (!ok) {
                    exceptionHelper.throwLocalizedMessage(
                            "error.menuService.scheduleNotWithinOpeningHours");
                }
            }

            reqs.sort(Comparator.comparingInt(r -> r.start));
            plans.sort(Comparator.comparingInt(r -> r.start));

            for (NormRange r : reqs) {
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
                        exceptionHelper.throwLocalizedMessage(
                                "error.menuService.scheduleIncomplete");
                    }
                    if (seg.start < cursor) {
                        exceptionHelper.throwLocalizedMessage(
                                "error.menuService.schedulesCollide");
                    }
                    cursor = seg.end;
                }
                if (cursor < r.end) {
                    exceptionHelper.throwLocalizedMessage(
                            "error.menuService.scheduleIncomplete");
                }
            }
        }
    }

    private Map<DayOfWeek, List<NormRange>> buildRequiredSegments(
            Map<DayOfWeek, TimeRange> operatingHours) {
        var map = new EnumMap<DayOfWeek, List<NormRange>>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            TimeRange op = operatingHours.get(day);
            if (op == null || !op.isAvailable()) {
                continue;
            }
            LocalTime start = op.getStartTime();
            LocalTime end = op.getEndTime();

            if (!start.isAfter(end)) {
                addReq(map, day, toMinutes(start), toMinutes(end));
            } else {
                addReq(map, day, toMinutes(start), MINUTES_PER_DAY);
                DayOfWeek next = day.plus(1);
                addReq(map, next, 0, toMinutes(end));
            }
        }
        return map;
    }

    private void addReq(Map<DayOfWeek, List<NormRange>> m,
                        DayOfWeek d, int s, int e) {
        m.computeIfAbsent(d, x -> new ArrayList<>()).add(new NormRange(s, e));
    }

    private Map<DayOfWeek, List<NormRange>> buildPlannedSegments(
            Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay) {
        var map = new EnumMap<DayOfWeek, List<NormRange>>(DayOfWeek.class);
        for (var ent : scheduleByDay.entrySet()) {
            DayOfWeek day = ent.getKey();
            for (ScheduleEntry se : ent.getValue()) {
                LocalTime start = se.range.getStartTime();
                LocalTime end = se.range.getEndTime();
                if (!start.isAfter(end)) {
                    addPlan(map, day, toMinutes(start), toMinutes(end));
                } else {
                    addPlan(map, day, toMinutes(start), MINUTES_PER_DAY);
                    DayOfWeek next = day.plus(1);
                    addPlan(map, next, 0, toMinutes(end));
                }
            }
        }
        return map;
    }

    private void addPlan(Map<DayOfWeek, List<NormRange>> m,
                         DayOfWeek d, int s, int e) {
        m.computeIfAbsent(d, x -> new ArrayList<>()).add(new NormRange(s, e));
    }

    private int toMinutes(LocalTime t) {
        return t.getHour() * 60 + t.getMinute();
    }

    private static final class NormRange {
        final int start, end;

        NormRange(int s, int e) {
            start = s;
            end = e;
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
