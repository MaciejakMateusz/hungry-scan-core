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

    SettingsRepository settingsRepository;
    ExceptionHelper exceptionHelper;

    public void validateMenusPlans(List<MenuSimpleDTO> menuDTOs, Long restaurantId) throws LocalizedException {
        validateScheduleEmptiness(menuDTOs);

        Settings settings = settingsRepository.findByRestaurantId(restaurantId);
        LocalTime openingTime = settings.getOpeningTime();
        LocalTime closingTime = settings.getClosingTime();

        Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay = getScheduleByDay(menuDTOs, openingTime, closingTime);
        validatePlans(scheduleByDay, openingTime, closingTime);
    }

    private Map<DayOfWeek, List<ScheduleEntry>> getScheduleByDay(List<MenuSimpleDTO> menuDTOs,
                                                                 LocalTime openingTime,
                                                                 LocalTime closingTime) throws LocalizedException {
        Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay = new HashMap<>();
        for (MenuSimpleDTO menu : menuDTOs) {
            for (MenuPlanDTO plan : menu.plan()) {
                DayOfWeek day = plan.dayOfWeek();
                scheduleByDay
                        .computeIfAbsent(day, d -> new ArrayList<>())
                        .addAll(wrap(menu, plan.timeRanges(), openingTime, closingTime));
            }
        }
        return scheduleByDay;
    }

    private void validatePlans(Map<DayOfWeek, List<ScheduleEntry>> scheduleByDay,
                               LocalTime openingTime,
                               LocalTime closingTime) throws LocalizedException {
        for (DayOfWeek day : DayOfWeek.values()) {
            List<ScheduleEntry> entries = scheduleByDay.get(day);
            if (entries == null || entries.isEmpty()) {
                exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
                return;
            }

            entries.sort(Comparator.comparing(e -> e.range.getStartTime()));

            LocalTime cursor = getLocalTime(openingTime, entries);

            if (cursor.isBefore(closingTime)) {
                exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
            }
        }
    }

    private LocalTime getLocalTime(LocalTime openingTime, List<ScheduleEntry> entries) throws LocalizedException {
        LocalTime cursor = openingTime;
        for (ScheduleEntry e : entries) {
            LocalTime start = e.range.getStartTime();
            LocalTime end = e.range.getEndTime();

            if (start.isAfter(cursor)) {
                exceptionHelper.throwLocalizedMessage("error.menuService.scheduleIncomplete");
            }
            if (start.isBefore(cursor)) {
                exceptionHelper.throwLocalizedMessage("error.menuService.schedulesCollide");
            }
            cursor = end;
        }
        return cursor;
    }

    private void validateScheduleEmptiness(List<MenuSimpleDTO> menuDTOs) throws LocalizedException {
        boolean entireScheduleEmpty = menuDTOs.stream().allMatch(dto -> dto.plan().isEmpty());
        if (entireScheduleEmpty) {
            throw new LocalizedException("error.menuService.scheduleIncomplete");
        }
    }

    private List<ScheduleEntry> wrap(MenuSimpleDTO menu,
                                     Set<TimeRange> ranges,
                                     LocalTime opening,
                                     LocalTime closing) throws LocalizedException {
        List<ScheduleEntry> out = new ArrayList<>();
        for (TimeRange tr : ranges) {
            if (tr.getStartTime().isBefore(opening) || tr.getEndTime().isAfter(closing)) {
                throw new LocalizedException("error.menuService.scheduleNotWithinOpeningHours");
            }
            out.add(new ScheduleEntry(menu, tr));
        }
        return out;
    }

    private static class ScheduleEntry {
        final MenuSimpleDTO menu;
        final TimeRange range;

        ScheduleEntry(MenuSimpleDTO m, TimeRange r) {
            this.menu = m;
            this.range = r;
        }
    }
}