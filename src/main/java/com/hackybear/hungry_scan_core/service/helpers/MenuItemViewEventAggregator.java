package com.hackybear.hungry_scan_core.service.helpers;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.interfaces.aggregators.MenuItemViewAggregation;
import com.hackybear.hungry_scan_core.repository.MenuItemViewEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MenuItemViewEventAggregator {

    private final MenuItemViewEventRepository viewEventRepository;

    public Set<MenuItemViewCountDTO> projectYearlyMenuItemViews(Long menuId, int year) {
        List<MenuItemViewAggregation> menuItemViews = viewEventRepository.aggregateByYear(menuId, year);
        return getMenuItemViewCountDTOS(menuItemViews);
    }

    public Set<MenuItemViewCountDTO> projectMonthlyMenuItemViews(Long menuId, int year, int month) {
        List<MenuItemViewAggregation> menuItemViews = viewEventRepository.aggregateByMonth(menuId, year, month);
        return getMenuItemViewCountDTOS(menuItemViews);
    }

    public Set<MenuItemViewCountDTO> projectWeeklyMenuItemViews(Long menuId, int year, int week) {
        List<MenuItemViewAggregation> menuItemViews = viewEventRepository.aggregateByWeek(menuId, year, week);
        return getMenuItemViewCountDTOS(menuItemViews);
    }

    public Set<MenuItemViewCountDTO> projectDailyMenuItemViews(Long menuId, LocalDate date) {
        List<MenuItemViewAggregation> menuItemViews = viewEventRepository.aggregateByDay(menuId, date);
        return getMenuItemViewCountDTOS(menuItemViews);
    }

    private static Set<MenuItemViewCountDTO> getMenuItemViewCountDTOS(List<MenuItemViewAggregation> menuItemViews) {
        Map<Long, MenuItemViewAggregation> aggregationByYear = menuItemViews
                .stream()
                .collect(Collectors.toMap(MenuItemViewAggregation::getId, Function.identity()));
        return consolidateViews(menuItemViews, aggregationByYear);
    }

    private static Set<MenuItemViewCountDTO> consolidateViews(List<MenuItemViewAggregation> menuItemViews,
                                                              Map<Long, MenuItemViewAggregation> aggregationByYear) {
        Set<MenuItemViewCountDTO> results = new HashSet<>();
        for (MenuItemViewAggregation aggregation : menuItemViews) {
            MenuItemViewAggregation event = aggregationByYear.get(aggregation.getId());
            MenuItemViewCountDTO viewCountDTO = getViewCountDTO(event);
            results.add(viewCountDTO);
        }
        return results;
    }

    private static MenuItemViewCountDTO getViewCountDTO(MenuItemViewAggregation event) {
        return new MenuItemViewCountDTO(
                event.getId(),
                event.getPl(),
                event.getEn(),
                event.getFr(),
                event.getDe(),
                event.getEs(),
                event.getUk(),
                event.getViews());
    }

}
