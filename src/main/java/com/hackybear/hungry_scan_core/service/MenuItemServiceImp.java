package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.*;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemViewEventRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemServiceImp implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final VariantRepository variantRepository;
    private final MenuItemMapper menuItemMapper;
    private final TranslatableMapper translatableMapper;
    private final AllergenMapper allergenMapper;
    private final LabelMapper labelMapper;
    private final IngredientMapper ingredientMapper;
    private final EntityManager entityManager;
    private final MenuItemViewEventRepository menuItemViewEventRepository;

    @Override
    public MenuItemFormDTO findById(Long id) throws LocalizedException {
        MenuItem menuItem = getMenuItem(id);
        return menuItemMapper.toFormDTO(menuItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#menuItemFormDTO.categoryId()")
    })
    public void save(MenuItemFormDTO menuItemFormDTO, Long activeMenuId) throws Exception {
        MenuItem menuItem = menuItemMapper.toMenuItem(menuItemFormDTO);
        Optional<Integer> maxDisplayOrder = menuItemRepository.findMaxDisplayOrder(menuItem.getCategory().getId());
        menuItem.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        menuItemRepository.save(menuItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#menuItemFormDTO.categoryId()")
    })
    public void update(MenuItemFormDTO menuItemFormDTO, Long activeMenuId) throws Exception {
        MenuItem existingMenuItem = getMenuItem(menuItemFormDTO.id());
        Long newCategoryId = menuItemFormDTO.categoryId();
        Category oldCategory = findCategoryByMenuItemId(existingMenuItem.getId());
        Category newCategory = findCategoryById(newCategoryId);
        updateMenuItem(existingMenuItem, menuItemFormDTO);
        switchCategory(existingMenuItem, oldCategory, newCategory);
        menuItemRepository.save(existingMenuItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#menuItemsDTOs.get(0).category().id()")
    })
    public void updateDisplayOrders(List<MenuItemSimpleDTO> menuItemsDTOs, Long activeMenuId) {
        List<MenuItem> menuItems = menuItemsDTOs.stream().map(menuItemMapper::toMenuItem).toList();
        for (MenuItem menuItem : menuItems) {
            menuItemRepository.updateDisplayOrders(menuItem.getId(), menuItem.getDisplayOrder());
        }
    }

    @Override
    public Set<MenuItemSimpleDTO> filterByName(String value) {
        String filterValue = "%" + value.toLowerCase() + "%";
        Set<MenuItem> menuItems = menuItemRepository.filterByName(filterValue);
        return menuItems.stream().map(menuItemMapper::toDTO).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#menuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#menuId"),
    })
    public void delete(Long id, Long menuId) throws LocalizedException {
        MenuItem existingMenuItem = getMenuItem(id);
        removeVariants(existingMenuItem);
        Category category = existingMenuItem.getCategory();
        removeMenuItem(category, existingMenuItem);
        Set<MenuItem> menuItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(category.getId());
        sortingHelper.reassignDisplayOrders(menuItems, menuItemRepository::saveAllAndFlush);
    }

    @Override
    public void persistViewEvent(Long menuItemId, Long activeMenuId) {
        MenuItemViewEvent menuItemViewEvent = new MenuItemViewEvent();
        menuItemViewEvent.setMenuItemId(menuItemId);
        menuItemViewEvent.setMenuId(activeMenuId);
        menuItemViewEventRepository.save(menuItemViewEvent);
    }

    private MenuItem getMenuItem(Long id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    private Category findCategoryById(Long id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    private Category findCategoryByMenuItemId(Long id) throws LocalizedException {
        return categoryRepository.findByMenuItemId(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFoundByMenuItem", id));
    }

    private void switchCategory(MenuItem existingMenuItem,
                                Category oldCategory,
                                Category newCategory) {
        Long existingMenuItemId = existingMenuItem.getId();
        if (Objects.isNull(existingMenuItemId)) {
            return;
        }
        if (oldCategory.getId().equals(newCategory.getId())) {
            return;
        }
        existingMenuItem = entityManager.merge(existingMenuItem);
        Optional<Integer> maxDisplayOrder = menuItemRepository.findMaxDisplayOrder(newCategory.getId());
        existingMenuItem.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        newCategory.addMenuItem(existingMenuItem);
        categoryRepository.save(newCategory);

        Set<MenuItem> oldCategoryItems = oldCategory.getMenuItems();
        oldCategoryItems.removeIf(item -> item.getId().equals(existingMenuItemId));
        sortingHelper.reassignDisplayOrders(oldCategoryItems, menuItemRepository::saveAll);
        categoryRepository.save(oldCategory);
    }

    private void updateMenuItem(MenuItem existing, MenuItemFormDTO dto) throws LocalizedException {
        existing.setImageName(dto.imageName());
        existing.setName(translatableMapper.toTranslatable(dto.name()));
        existing.setDescription(translatableMapper.toTranslatable(dto.description()));
        existing.setCategory(categoryRepository.findById(dto.categoryId()).orElseThrow());
        existing.setPrice(dto.price());
        existing.setLabels(dto.labels().stream()
                .map(labelMapper::toLabel).collect(Collectors.toSet()));
        existing.setAllergens(dto.allergens().stream()
                .map(allergenMapper::toAllergen).collect(Collectors.toSet()));
        existing.setAdditionalIngredients(dto.additionalIngredients().stream()
                .map(ingredientMapper::toIngredient).collect(Collectors.toSet()));
        existing.setAvailable(dto.available());
        existing.setVisible(dto.visible());

        if (isPromoPriceInvalid(dto)) {
            exceptionHelper.throwLocalizedMessage("error.menuItemService.invalidPromoPrice");
        }
        existing.setPromoPrice(dto.promoPrice());
        existing.setBanners(dto.banners());
    }

    private void removeVariants(MenuItem menuItem) {
        if (!menuItem.getVariants().isEmpty()) {
            List<Variant> variants = variantRepository.findAllByMenuItemIdOrderByDisplayOrder(menuItem.getId());
            variantRepository.deleteAll(variants);
        }
    }

    private void removeMenuItem(Category category, MenuItem menuItem) {
        category.removeMenuItem(menuItem);
        categoryRepository.save(category);
        menuItemRepository.deleteById(menuItem.getId());
    }

    private boolean isPromoPriceInvalid(MenuItemFormDTO dto) {
        List<Banner> promo = dto.banners().stream().filter(banner -> banner.getId().equals("promo")).toList();
        return !promo.isEmpty() && (Objects.isNull(dto.promoPrice()) || dto.promoPrice().signum() == 0);
    }
}