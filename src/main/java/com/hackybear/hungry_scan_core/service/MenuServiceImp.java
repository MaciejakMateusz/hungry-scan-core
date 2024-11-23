package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.dto.mapper.ScheduleMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.MENUS_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.MENU_ID;

@Service
@Slf4j
public class MenuServiceImp implements MenuService {

    private final ExceptionHelper exceptionHelper;
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final ScheduleMapper scheduleMapper;

    public MenuServiceImp(ExceptionHelper exceptionHelper,
                          MenuRepository menuRepository, MenuMapper menuMapper, ScheduleMapper scheduleMapper) {
        this.exceptionHelper = exceptionHelper;
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    @Cacheable(value = MENUS_ALL, key = "#activeRestaurantId")
    public Set<MenuSimpleDTO> findAll(Long activeRestaurantId) throws LocalizedException {
        Set<Menu> menus = menuRepository.findAllByRestaurantId(activeRestaurantId);
        return menus.stream()
                .map(menuMapper::toDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    @Cacheable(value = MENU_ID, key = "#id")
    public MenuSimpleDTO findById(Long id, Long activeRestaurantId) throws LocalizedException {
        Menu menu = getById(id);
        validateOperation(menu.getRestaurantId(), activeRestaurantId);
        return menuMapper.toDTO(menu);
    }

    @Transactional
    @Override
    @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId")
    public void save(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = menuMapper.toMenu(menuDTO);
        menu.setRestaurantId(activeRestaurantId);
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
        menu.setName(menuDTO.name());
        menu.setSchedule(scheduleMapper.toSchedule(menuDTO.schedule()));
        menu.setAllDay(menuDTO.allDay());
        menuRepository.saveAndFlush(menu);
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
        if (!existingMenu.getCategories().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.userService.menuNotEmpty");
            return;
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

    //todo Scheduler validator

}