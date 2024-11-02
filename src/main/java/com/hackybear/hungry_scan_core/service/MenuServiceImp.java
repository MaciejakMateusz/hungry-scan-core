package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MenuServiceImp implements MenuService {

    private final UserService userService;
    private final ExceptionHelper exceptionHelper;
    private final MenuRepository menuRepository;

    public MenuServiceImp(UserService userService,
                          ExceptionHelper exceptionHelper,
                          MenuRepository menuRepository) {
        this.userService = userService;
        this.exceptionHelper = exceptionHelper;
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Menu> findAll() throws LocalizedException {
        Long activeRestaurantId = userService.getActiveRestaurantId();
        return menuRepository.findAllByRestaurantId(activeRestaurantId);
    }

    @Override
    public Long countAll() throws LocalizedException {
        Long activeRestaurantId = userService.getActiveRestaurantId();
        return menuRepository.countAllByRestaurantId(activeRestaurantId);
    }


    @Override
    public Menu findById(Long id) throws LocalizedException {
        return menuRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound", id));
    }

    @Transactional
    @Override
    public void save(Menu menu) throws Exception {
        Long activeRestaurantId = userService.getActiveRestaurantId();
        menu.setRestaurantId(activeRestaurantId);
        menuRepository.save(menu);
    }

    @Transactional
    @Override
    public void delete(Long id) throws LocalizedException {
        Menu existingMenu = findById(id);
        if (!existingMenu.getCategories().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.userService.menuNotEmpty");
            return;
        }
        menuRepository.delete(existingMenu);
    }

}