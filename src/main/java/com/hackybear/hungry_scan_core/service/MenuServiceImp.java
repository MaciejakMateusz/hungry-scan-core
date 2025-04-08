package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

    private final ExceptionHelper exceptionHelper;
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final SettingsRepository settingsRepository;

    @Override
    @Cacheable(value = MENUS_ALL, key = "#activeRestaurantId")
    public Set<MenuSimpleDTO> findAll(Long activeRestaurantId) throws LocalizedException {
        Set<Menu> menus = menuRepository.findAllByRestaurantId(activeRestaurantId);
        return menus.stream()
                .map(menuMapper::toSimpleDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    @Cacheable(value = MENU_ID, key = "#id")
    public MenuSimpleDTO findById(Long id, Long activeRestaurantId) throws LocalizedException {
        Menu menu = getById(id);
        validateOperation(menu.getRestaurantId(), activeRestaurantId);
        return menuMapper.toSimpleDTO(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getId()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void save(MenuSimpleDTO menuDTO, User currentUser) throws Exception {
        Menu menu = menuMapper.toMenu(menuDTO);
        menu.setRestaurantId(currentUser.getActiveRestaurantId());
        menuRepository.save(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = MENU_ID, key = "#menuDTO.id()")
    })
    public void update(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = getById(menuDTO.id());
        validateOperation(menu.getRestaurantId(), activeRestaurantId);
        validateSchedule(menuDTO, activeRestaurantId);
        menu.setName(menuDTO.name());
        menu.setStandard(menuDTO.standard());
        menu.setPlan(menuDTO.plan());
        menuRepository.saveAndFlush(menu);
    }

    @Transactional
    @Override
    @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId")
    public void updatePlans(List<MenuSimpleDTO> menuDTOs, Long activeRestaurantId) throws LocalizedException {
        validateMenusPlans(menuDTOs);
        Set<Menu> existing = menuRepository.findAllByRestaurantId(activeRestaurantId);
        for (Menu menu : existing) {
            for (MenuSimpleDTO menuDTO : menuDTOs) {
                if (menu.getId().equals(menuDTO.id())) {
                    menu.setPlan(menuDTO.plan());
                    menuRepository.save(menu);
                    break;
                }
            }
        }
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = MENU_ID, key = "#newId")
    })
    public void switchStandard(Long newId, Long activeRestaurantId) {
        menuRepository.resetStandardMenus(activeRestaurantId);
        menuRepository.switchStandard(newId);
    }

    private void validateMenusPlans(List<MenuSimpleDTO> menuDTOs) throws LocalizedException {
        Map<DayOfWeek, List<TimeRange>> scheduleMap = new HashMap<>();

        for (MenuSimpleDTO menuDTO : menuDTOs) {
            if (menuDTO.standard() || menuDTO.plan() == null) continue;

            for (Map.Entry<DayOfWeek, TimeRange> entry : menuDTO.plan().entrySet()) {
                DayOfWeek day = entry.getKey();
                TimeRange newRange = entry.getValue();

                if (newRange == null) continue;

                List<TimeRange> existingRanges = scheduleMap.getOrDefault(day, new ArrayList<>());
                for (TimeRange existing : existingRanges) {
                    if (isOverlapping(existing, newRange)) {
                        exceptionHelper.throwLocalizedMessage("error.menuService.schedulesCollide");
                    }
                }

                existingRanges.add(newRange);
                scheduleMap.put(day, existingRanges);
            }
        }
    }

    private boolean isOverlapping(TimeRange a, TimeRange b) {
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = MENU_ID, key = "#id")
    })
    public void delete(Long id, Long activeRestaurantId) throws LocalizedException {
        Menu existingMenu = getById(id);
        validateOperation(existingMenu.getRestaurantId(), activeRestaurantId);
        if (existingMenu.isStandard()) {
            exceptionHelper.throwLocalizedMessage("error.menuService.illegalMenuRemoval");
        }
        menuRepository.delete(existingMenu);
    }

    private Menu getById(Long id) throws LocalizedException {
        return menuRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound"));
    }

    private void validateOperation(Long restaurantId, Long activeRestaurantId) throws LocalizedException {
        if (!Objects.equals(restaurantId, activeRestaurantId)) {
            exceptionHelper.throwLocalizedMessage("error.general.unauthorizedOperation");
        }
    }

    private void validateSchedule(MenuSimpleDTO menuDTO, Long restaurantId) throws LocalizedException {
        if (menuDTO.standard()) {
            return;
        }
        validateWithinOpeningHours(menuDTO, restaurantId);
    }

    private void validateWithinOpeningHours(MenuSimpleDTO dto, Long activeRestaurantId) throws LocalizedException {
        if (Objects.isNull(dto)) {
            return;
        }
        Map<DayOfWeek, TimeRange> plan = dto.plan();
        List<TimeRange> timeRanges = plan.values().stream().toList();
        Settings settings = settingsRepository.findByRestaurantId(activeRestaurantId);
        TimeRange openingRange = new TimeRange(settings.getOpeningTime(), settings.getClosingTime());
        boolean isWithinOpeningHours = timeRanges.stream().allMatch(openingRange::includes);
        if (!isWithinOpeningHours) {
            exceptionHelper.throwLocalizedMessage("error.menuService.scheduleNotWithinOpeningHours");
        }
    }
}