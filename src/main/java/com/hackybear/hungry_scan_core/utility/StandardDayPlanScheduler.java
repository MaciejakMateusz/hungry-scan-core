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
        processStandardPlan(allMenus, standardMenu, hours);
    }

    public void mapStandardPlan(List<MenuSimpleDTO> allMenus, TimeRange openingHours) throws LocalizedException {
        Menu standardMenu = findStandardMenu(allMenus);
        OperatingHours hours = new OperatingHours(openingHours.getStartTime(), openingHours.getEndTime());
        processStandardPlan(allMenus, standardMenu, hours);
    }

    private void processStandardPlan(List<MenuSimpleDTO> allMenus, Menu standardMenu, OperatingHours hours) throws LocalizedException {
        Map<DayOfWeek, List<TimeRange>> blocked = collectBlockedTimeRanges(allMenus);
        Map<DayOfWeek, List<TimeRange>> openPerDay = computeOpenWindows(hours);
        List<StandardDayPlan> plans = generateStandardDayPlans(standardMenu, blocked, openPerDay);
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
        Settings settings = settingsRepository.findByRestaurantId(
                standardMenu.getRestaurant().getId());
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
                .forEach(menu ->
                        menu.getPlan().forEach((day, range) -> {
                            if (!range.getStartTime().isAfter(range.getEndTime())) {
                                blocked.get(day).add(range);
                            } else {
                                blocked.get(day).add(
                                        new TimeRange(range.getStartTime(), LocalTime.MAX)
                                );
                                blocked.get(day).add(
                                        new TimeRange(LocalTime.MIN, range.getEndTime())
                                );
                            }
                        })
                );
        return blocked;
    }

    private Map<DayOfWeek, List<TimeRange>> computeOpenWindows(OperatingHours hours) {
        Map<DayOfWeek, List<TimeRange>> openPerDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            openPerDay.put(d, new ArrayList<>());
        }

        if (!hours.open().isAfter(hours.close())) {
            for (DayOfWeek d : DayOfWeek.values()) {
                openPerDay.get(d).add(new TimeRange(hours.open(), hours.close()));
            }
        } else {
            for (DayOfWeek d : DayOfWeek.values()) {
                openPerDay.get(d).add(new TimeRange(hours.open(), LocalTime.MAX));
                openPerDay.get(d).add(new TimeRange(LocalTime.MIN, hours.close()));
            }
        }

        return openPerDay;
    }

    private List<StandardDayPlan> generateStandardDayPlans(Menu standardMenu,
                                                           Map<DayOfWeek, List<TimeRange>> blocked,
                                                           Map<DayOfWeek, List<TimeRange>> openPerDay) {
        return Arrays.stream(DayOfWeek.values())
                .map(d -> buildDayPlan(
                        standardMenu,
                        d,
                        mergeRanges(blocked.get(d)),
                        mergeRanges(openPerDay.get(d))
                ))
                .collect(Collectors.toList());
    }

    private StandardDayPlan buildDayPlan(Menu standardMenu,
                                         DayOfWeek day,
                                         List<TimeRange> mergedBlocked,
                                         List<TimeRange> mergedOpen) {
        List<TimeRange> available = new ArrayList<>();
        for (TimeRange window : mergedOpen) {
            available.addAll(subtract(window, mergedBlocked));
        }

        StandardDayPlan plan = new StandardDayPlan();
        plan.setMenu(standardMenu);
        plan.setDayOfWeek(day);
        plan.setTimeRanges(
                available.stream().map(tr -> {
                    DayTimeRange etr = new DayTimeRange();
                    etr.setStartTime(tr.getStartTime());
                    etr.setEndTime(tr.getEndTime());
                    etr.setStandardDayPlan(plan);
                    return etr;
                }).collect(Collectors.toList())
        );
        return plan;
    }

    private List<TimeRange> mergeRanges(List<TimeRange> input) {
        if (input.isEmpty()) return Collections.emptyList();
        List<TimeRange> sorted = new ArrayList<>(input);
        sorted.sort(Comparator.comparing(TimeRange::getStartTime));
        LinkedList<TimeRange> out = new LinkedList<>();
        out.add(sorted.getFirst());
        for (int i = 1; i < sorted.size(); i++) {
            TimeRange last = out.getLast(), next = sorted.get(i);
            if (!next.getStartTime().isAfter(last.getEndTime())) {
                LocalTime end = last.getEndTime().isAfter(next.getEndTime())
                        ? last.getEndTime() : next.getEndTime();
                out.removeLast();
                out.add(new TimeRange(last.getStartTime(), end));
            } else {
                out.add(next);
            }
        }
        return out;
    }

    private List<TimeRange> subtract(TimeRange full, List<TimeRange> blocks) {
        List<TimeRange> avail = new ArrayList<>();
        LocalTime cursor = full.getStartTime();
        for (TimeRange b : blocks) {
            if (b.getEndTime().isBefore(full.getStartTime()) ||
                    b.getStartTime().isAfter(full.getEndTime())) {
                continue;
            }
            LocalTime bStart = b.getStartTime().isBefore(full.getStartTime())
                    ? full.getStartTime() : b.getStartTime();
            LocalTime bEnd = b.getEndTime().isAfter(full.getEndTime())
                    ? full.getEndTime() : b.getEndTime();
            if (cursor.isBefore(bStart)) {
                avail.add(new TimeRange(cursor, bStart));
            }
            if (bEnd.isAfter(cursor)) {
                cursor = bEnd;
            }
        }
        if (cursor.isBefore(full.getEndTime())) {
            avail.add(new TimeRange(cursor, full.getEndTime()));
        }
        return avail;
    }

    private void saveStandardDayPlans(Menu standardMenu, List<StandardDayPlan> plans)
            throws LocalizedException {
        Menu existing = menuRepository.findById(standardMenu.getId())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"));
        existing.setStandardDayPlan(plans);
        menuRepository.save(existing);
    }

    private record OperatingHours(LocalTime open, LocalTime close) {
    }
}
