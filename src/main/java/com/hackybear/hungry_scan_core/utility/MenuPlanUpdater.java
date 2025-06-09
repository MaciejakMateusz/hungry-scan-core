package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MenuPlanUpdater {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    public void updateMenusPlans(Restaurant restaurant, RestaurantDTO restaurantDTO) throws LocalizedException {
        if (openingHoursUnchanged(restaurant, restaurantDTO)) {
            return;
        }

        Set<Menu> allMenus = loadAllMenus(restaurant.getId());
        TimeRange openingTimes = buildOpeningTimeRange(restaurantDTO);

        adjustPlansForNonStandard(allMenus, openingTimes);

    }

    private boolean openingHoursUnchanged(Restaurant restaurant, RestaurantDTO dto) {
        LocalTime oldOpen = restaurant.getSettings().getOpeningTime();
        LocalTime oldClose = restaurant.getSettings().getClosingTime();
        LocalTime newOpen = dto.settings().openingTime();
        LocalTime newClose = dto.settings().closingTime();
        return oldOpen.equals(newOpen) && oldClose.equals(newClose);
    }

    private Set<Menu> loadAllMenus(Long restaurantId) {
        return Optional.ofNullable(menuRepository.findAllByRestaurantId(restaurantId))
                .map(HashSet::new)
                .orElseGet(HashSet::new);
    }

    private TimeRange buildOpeningTimeRange(RestaurantDTO dto) {
        LocalTime open = dto.settings().openingTime();
        LocalTime close = dto.settings().closingTime();
        return new TimeRange(open, close);
    }

    private List<MenuSimpleDTO> toSimpleDTOs(Set<Menu> menus) {
        return menus.stream()
                .map(menuMapper::toSimpleDTO)
                .toList();
    }

    private void adjustPlansForNonStandard(Set<Menu> menus, TimeRange openingTimes) {

    }

    private Set<MenuPlan> nonNullPlan(Set<MenuPlan> plan) {
        return plan != null ? plan : Collections.emptySet();
    }

}