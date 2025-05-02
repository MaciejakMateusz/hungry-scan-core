package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.DayTimeRange;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.entity.StandardDayPlan;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StandardDayPlanScheduler {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final SettingsRepository settingsRepository;
    private final ExceptionHelper exceptionHelper;

    public void mapStandardPlan(List<MenuSimpleDTO> allMenus) throws LocalizedException {
        Menu standardMenu = findStandardMenu(allMenus);
        OperatingHours hours = fetchOperatingHours(standardMenu);
        Map<DayOfWeek, List<TimeRange>> blocked = collectBlockedTimeRanges(allMenus);
        List<StandardDayPlan> plans = generateStandardDayPlans(standardMenu, blocked, hours);
        saveStandardDayPlans(standardMenu, plans);
    }

    private Menu findStandardMenu(List<MenuSimpleDTO> menuDTOs) throws LocalizedException {
        return menuDTOs.stream()
                .filter(MenuSimpleDTO::standard)
                .findFirst()
                .map(menuMapper::toMenu)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"));
    }

    private OperatingHours fetchOperatingHours(Menu standardMenu) {
        Settings settings = settingsRepository.findByRestaurantId(standardMenu.getRestaurant().getId());
        return new OperatingHours(settings.getOpeningTime(), settings.getClosingTime());
    }

    private Map<DayOfWeek, List<TimeRange>> collectBlockedTimeRanges(List<MenuSimpleDTO> dtos) {
        Map<DayOfWeek, List<TimeRange>> blocked = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            blocked.put(d, new ArrayList<>());
        }

        dtos.stream()
                .filter(dto -> !dto.standard())
                .map(menuMapper::toMenu)
                .forEach(menu -> menu.getPlan().forEach((day, range) -> {
                    if (!range.getStartTime().isAfter(range.getEndTime())) {
                        blocked.get(day).add(range);
                    } else {
                        blocked.get(day).add(
                                new TimeRange(range.getStartTime(), LocalTime.MAX)
                        );
                        DayOfWeek tomorrow = day.plus(1);
                        blocked.get(tomorrow).add(
                                new TimeRange(LocalTime.MIN, range.getEndTime())
                        );
                    }
                }));

        return blocked;
    }

    private List<StandardDayPlan> generateStandardDayPlans(Menu standardMenu,
                                                           Map<DayOfWeek, List<TimeRange>> blocked,
                                                           OperatingHours hours) {
        return Arrays.stream(DayOfWeek.values())
                .map(day -> buildDayPlan(standardMenu, day, blocked.get(day), hours))
                .collect(Collectors.toList());
    }

    private StandardDayPlan buildDayPlan(Menu standardMenu,
                                         DayOfWeek day,
                                         List<TimeRange> dailyBlocked,
                                         OperatingHours hours) {
        List<TimeRange> mergedBlocked = mergeRanges(dailyBlocked);

        List<TimeRange> openWindows = new ArrayList<>();
        if (!hours.open().isAfter(hours.close())) {
            openWindows.add(new TimeRange(hours.open(), hours.close()));
        } else {
            openWindows.add(new TimeRange(hours.open(), LocalTime.MAX));
            openWindows.add(new TimeRange(LocalTime.MIN, hours.close()));
        }

        List<TimeRange> available = new ArrayList<>();
        for (TimeRange window : openWindows) {
            available.addAll(subtract(window, mergedBlocked));
        }

        StandardDayPlan plan = new StandardDayPlan();
        plan.setMenu(standardMenu);
        plan.setDayOfWeek(day);
        List<DayTimeRange> entityRanges = available.stream()
                .map(tr -> {
                    DayTimeRange etr = new DayTimeRange();
                    etr.setStartTime(tr.getStartTime());
                    etr.setEndTime(tr.getEndTime());
                    etr.setStandardDayPlan(plan);
                    return etr;
                })
                .collect(Collectors.toList());
        plan.setTimeRanges(entityRanges);
        return plan;
    }


    private void saveStandardDayPlans(Menu standardMenu, List<StandardDayPlan> plans) {
        standardMenu.setStandardDayPlan(plans);
        menuRepository.save(standardMenu);
    }

    private record OperatingHours(LocalTime open, LocalTime close) {
    }

    private List<TimeRange> mergeRanges(List<TimeRange> input) {
        if (input.isEmpty()) return Collections.emptyList();

        List<TimeRange> sorted = new ArrayList<>(input);
        sorted.sort(Comparator.comparing(TimeRange::getStartTime));

        LinkedList<TimeRange> result = new LinkedList<>();
        result.add(sorted.getFirst());

        for (int i = 1; i < sorted.size(); i++) {
            TimeRange last = result.getLast();
            TimeRange next = sorted.get(i);

            if (!next.getStartTime().isAfter(last.getEndTime())) {
                LocalTime maxEnd = last.getEndTime().isAfter(next.getEndTime())
                        ? last.getEndTime() : next.getEndTime();
                result.removeLast();
                result.add(new TimeRange(last.getStartTime(), maxEnd));
            } else {
                result.add(next);
            }
        }

        return result;
    }

    private List<TimeRange> subtract(TimeRange fullDay, List<TimeRange> blocks) {
        List<TimeRange> avail = new ArrayList<>();
        LocalTime cursor = fullDay.getStartTime();

        for (TimeRange b : blocks) {
            if (b.getEndTime().isBefore(fullDay.getStartTime()) ||
                    b.getStartTime().isAfter(fullDay.getEndTime())) {
                continue;
            }

            LocalTime start = b.getStartTime().isBefore(fullDay.getStartTime())
                    ? fullDay.getStartTime() : b.getStartTime();
            LocalTime end = b.getEndTime().isAfter(fullDay.getEndTime())
                    ? fullDay.getEndTime() : b.getEndTime();

            if (cursor.isBefore(start)) {
                avail.add(new TimeRange(cursor, start));
            }
            cursor = end.isAfter(cursor) ? end : cursor;
        }

        if (cursor.isBefore(fullDay.getEndTime())) {
            avail.add(new TimeRange(cursor, fullDay.getEndTime()));
        }

        return avail;
    }
}