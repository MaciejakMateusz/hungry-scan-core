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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    public Set<MenuSimpleDTO> findAll(Long activeRestaurantId) throws LocalizedException {
        Set<Menu> menus = menuRepository.findAllByRestaurantId(activeRestaurantId);
        return menus.stream()
                .map(menuMapper::toDTO)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public MenuSimpleDTO findById(Long id) throws LocalizedException {
        Menu menu = getById(id);
        return menuMapper.toDTO(menu);
    }

    @Transactional
    @Override
    public void save(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = menuMapper.toMenu(menuDTO);
        menu.setRestaurantId(activeRestaurantId);
        menuRepository.save(menu);
    }

    @Transactional
    @Override
    public void update(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = getById(menuDTO.id());
        menu.setName(menuDTO.name());
        menu.setSchedule(scheduleMapper.toSchedule(menuDTO.schedule()));
        menu.setAllDay(menuDTO.allDay());
        menuRepository.save(menu);
    }

    @Transactional
    @Override
    public void delete(Long id, Long activeRestaurantId) throws LocalizedException {
        Menu existingMenu = getById(id);
        if (!existingMenu.getCategories().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.userService.menuNotEmpty");
            return;
        }
        menuRepository.delete(existingMenu);
    }

    private Menu getById(Long id) throws LocalizedException {
        return menuRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound", id));
    }

    //todo Scheduler validator

}