package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuDeepCopyMapper;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuColorRepository;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.validator.MenuPlanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.DeepCopyUtils.constructDuplicateName;
import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

    private final ExceptionHelper exceptionHelper;
    private final MenuRepository menuRepository;
    private final MenuPlanValidator menuPlanValidator;
    private final MenuMapper menuMapper;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuDeepCopyMapper menuDeepCopyMapper;
    private final S3Service s3Service;

    static final String S3_PATH = "menuItems";
    private final MenuColorRepository menuColorRepository;

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
        validateOperation(menu.getRestaurant().getId(), activeRestaurantId);
        return menuMapper.toSimpleDTO(menu);
    }

    @Override
    @Cacheable(value = MENU_CUSTOMER_ID, key = "#id")
    public MenuCustomerDTO projectPlannedMenu(Long id) throws LocalizedException {
        Menu menu = getById(id);
        return menuMapper.toCustomerDTO(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getActiveRestaurantId()")
    })
    public void save(MenuFormDTO menuFormDTO, User currentUser) throws Exception {
        validateUniqueness(menuFormDTO.name(), currentUser.getActiveRestaurantId());
        Menu menu = menuMapper.toMenu(menuFormDTO);
        Restaurant restaurant = restaurantRepository.findById(currentUser.getActiveRestaurantId()).orElseThrow();
        menu.setRestaurant(restaurant);
        menu.setTheme(Theme.COLOR_318E41);
        menu.setMessage(getMenuMessage());
        menu = menuRepository.save(menu);
        currentUser.setActiveMenuId(menu.getId());
        userRepository.save(currentUser);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = MENU_ID, key = "#menuDTO.id()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#activeRestaurantId")
    })
    public void update(MenuFormDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = getById(menuDTO.id());
        if (!Objects.equals(menu.getName(), menuDTO.name())) {
            validateUniqueness(menuDTO.name(), activeRestaurantId);
        }
        validateOperation(menu.getRestaurant().getId(), activeRestaurantId);
        menuMapper.updateFromFormDTO(menuDTO, menu);
        menuRepository.saveAndFlush(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = MENU_ID, key = "#menuDTO.id()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#activeRestaurantId")
    })
    public void personalize(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = getById(menuDTO.id());
        validateOperation(menu.getRestaurant().getId(), activeRestaurantId);
        menuMapper.updateFromSimpleDTO(menuDTO, menu);
        menuRepository.saveAndFlush(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = USER_RESTAURANT, key = "#activeRestaurantId")
    })
    public void updatePlans(List<MenuSimpleDTO> menuDTOs, Long activeRestaurantId) throws LocalizedException {
        menuPlanValidator.validateMenusPlans(menuDTOs, activeRestaurantId);
        Set<Menu> existingMenus = menuRepository.findAllByRestaurantId(activeRestaurantId);
        Map<Long, Menu> menuById = existingMenus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));
        List<Menu> toSave = new ArrayList<>(menuDTOs.size());
        for (MenuSimpleDTO dto : menuDTOs) {
            Menu target = menuById.get(dto.id());
            menuMapper.updateFromSimpleDTO(dto, target);
            target.setColor(menuColorRepository.findById(dto.color().id())
                    .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                            "error.menuColorService.menuColorNotFound")));
            toSave.add(target);
        }
        menuRepository.saveAll(toSave);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = MENU_ID, key = "#currentUser.getActiveMenuId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    public void switchStandard(User currentUser) {
        menuRepository.resetStandardMenus(currentUser.getActiveRestaurantId());
        menuRepository.switchStandard(currentUser.getActiveMenuId());
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = MENU_ID, key = "#currentUser.getActiveMenuId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    public void delete(User currentUser) throws LocalizedException {
        Menu existingMenu = getById(currentUser.getActiveMenuId());
        Long activeRestaurantId = currentUser.getActiveRestaurantId();
        validateOperation(existingMenu.getRestaurant().getId(), activeRestaurantId);
        if (existingMenu.isStandard()) {
            exceptionHelper.throwLocalizedMessage("error.menuService.illegalMenuRemoval");
        }
        Long standardId = menuRepository.findStandardIdByRestaurantId(activeRestaurantId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"));
        currentUser.setActiveMenuId(standardId);
        menuRepository.delete(existingMenu);
        userRepository.save(currentUser);
        removeMenuItemsFiles(existingMenu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = MENU_ID, key = "#currentUser.getActiveMenuId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    public void duplicate(User currentUser) throws LocalizedException {
        Menu src = menuRepository.findByIdWithAllGraph(currentUser.getActiveMenuId())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"));

        Menu copy = menuDeepCopyMapper.duplicateMenu(src);
        copy.setRestaurant(src.getRestaurant());
        copy.setName(constructDuplicateName(src.getName()));
        validateUniqueness(copy.getName(), currentUser.getActiveRestaurantId());

        cascadeDuplicateCategory(copy);

        copy = menuRepository.saveAndFlush(copy);

        copy.getCategories().forEach(c ->
                c.getMenuItems().forEach(mi -> {
                    Long oldId = mi.getSourceId();
                    Long newId = mi.getId();
                    if (oldId != null && newId != null) {
                        s3Service.copyFile(S3_PATH, oldId, newId);
                    }
                })
        );

        currentUser.setActiveMenuId(copy.getId());
        userRepository.save(currentUser);
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

    private void validateUniqueness(String menuName, Long restaurantId) throws LocalizedException {
        if (menuRepository.existsByRestaurantIdAndName(restaurantId, menuName)) {
            exceptionHelper.throwLocalizedMessage("error.menuService.uniqueNameViolation");
        }
    }

    private void removeMenuItemsFiles(Menu existingMenu) {
        List<MenuItem> menuPositions = new ArrayList<>();
        for (Category category : existingMenu.getCategories()) {
            menuPositions.addAll(category.getMenuItems());
        }
        List<Long> menuItemIds = menuPositions.stream().map(MenuItem::getId).toList();
        s3Service.deleteAllFiles(S3_PATH, menuItemIds);
    }

    private void cascadeDuplicateCategory(Menu copy) {
        copy.getCategories().forEach(category -> {
            category.setMenu(copy);
            category.getMenuItems().forEach(item -> {
                item.setCategory(category);
                item.getVariants().forEach(variant -> variant.setMenuItem(item));
            });
        });
    }

    private static Translatable getMenuMessage() {
        return new Translatable()
                .withPl("Welcome!")
                .withEn("Enjoy your meal!");
    }
}