package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuDeepCopyMapper;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.utility.StandardDayPlanScheduler;
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

import static com.hackybear.hungry_scan_core.utility.DeepCopyUtils.constructDuplicateName;
import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImp implements MenuService {

    private final ExceptionHelper exceptionHelper;
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuDeepCopyMapper menuDeepCopyMapper;
    private final StandardDayPlanScheduler standardDayPlanScheduler;
    private final S3Service s3Service;

    private static final String S3_PATH = "menuItems";

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
    public MenuCustomerDTO projectPlannedMenu(Long id, Long activeRestaurantId) throws LocalizedException {
        Menu menu = getById(id);
        validateOperation(menu.getRestaurant().getId(), activeRestaurantId);
        return menuMapper.toCustomerDTO(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getActiveRestaurantId()")
    })
    public void save(MenuSimpleDTO menuDTO, User currentUser) throws Exception {
        validateUniqueness(menuDTO.name(), currentUser.getActiveRestaurantId());
        Menu menu = menuMapper.toMenu(menuDTO);
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
    public void update(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception {
        Menu menu = getById(menuDTO.id());
        if (!Objects.equals(menu.getName(), menuDTO.name())) {
            validateUniqueness(menuDTO.name(), activeRestaurantId);
        }
        validateOperation(menu.getRestaurant().getId(), activeRestaurantId);
        menu.setName(menuDTO.name());
        menuRepository.saveAndFlush(menu);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId"),
            @CacheEvict(value = USER_RESTAURANT, key = "#activeRestaurantId")
    })
    public void updatePlans(List<MenuSimpleDTO> menuDTOs, Long activeRestaurantId) throws LocalizedException {
        validateMenusPlans(menuDTOs);

        Map<Long, Map<DayOfWeek, TimeRange>> dtoPlanMap = menuDTOs.stream()
                .collect(Collectors.toMap(MenuSimpleDTO::id, MenuSimpleDTO::plan));

        Set<Menu> existingMenus = menuRepository.findAllByRestaurantId(activeRestaurantId);

        List<Menu> toSave = existingMenus.stream()
                .filter(menu -> {
                    Map<DayOfWeek, TimeRange> newPlan = dtoPlanMap.get(menu.getId());
                    return newPlan != null && !Objects.equals(menu.getPlan(), newPlan);
                })
                .peek(menu -> menu.setPlan(dtoPlanMap.get(menu.getId())))
                .collect(Collectors.toList());

        if (!toSave.isEmpty()) {
            menuRepository.saveAll(toSave);
        }
        standardDayPlanScheduler.mapStandardPlan(menuDTOs);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = MENUS_ALL, key = "#currentUser.getActiveRestaurantId()"),
            @CacheEvict(value = MENU_ID, key = "#currentUser.getActiveMenuId()"),
            @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    })
    public void switchStandard(User currentUser) throws LocalizedException {
        menuRepository.resetStandardMenus(currentUser.getActiveRestaurantId());
        menuRepository.switchStandard(currentUser.getActiveMenuId());
        Menu menu = menuRepository.findById(currentUser.getActiveMenuId()).orElseThrow();
        menu.setPlan(new HashMap<>());
        menuRepository.saveAndFlush(menu);
        List<Menu> menus = menuRepository.findAllByRestaurantId(currentUser.getActiveRestaurantId()).stream().toList();
        List<MenuSimpleDTO> menuDTOs = menus.stream().map(menuMapper::toSimpleDTO).toList();
        standardDayPlanScheduler.mapStandardPlan(menuDTOs);
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

        String srcName = src.getName();
        copy.setName(constructDuplicateName(srcName));

        validateUniqueness(copy.getName(), currentUser.getActiveRestaurantId());

        for (Category cat : copy.getCategories()) {
            cat.setMenu(copy);
            for (MenuItem item : cat.getMenuItems()) {
                item.setCategory(cat);
                for (Variant v : item.getVariants()) {
                    v.setMenuItem(item);
                }
            }
        }
        copy = menuRepository.save(copy);
        currentUser.setActiveMenuId(copy.getId());
        userRepository.save(currentUser);
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

    private static Translatable getMenuMessage() {
        return new Translatable()
                .withDefaultTranslation("Welcome!")
                .withTranslationEn("Enjoy your meal!");
    }
}