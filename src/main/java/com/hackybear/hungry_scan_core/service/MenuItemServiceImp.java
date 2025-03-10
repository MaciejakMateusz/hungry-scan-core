package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.*;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.MenuItemViewEvent;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemViewEventRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Slf4j
@Service
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

    public MenuItemServiceImp(MenuItemRepository menuItemRepository,
                              CategoryRepository categoryRepository,
                              ExceptionHelper exceptionHelper,
                              SortingHelper sortingHelper,
                              VariantRepository variantRepository, MenuItemMapper menuItemMapper, TranslatableMapper translatableMapper, AllergenMapper allergenMapper, LabelMapper labelMapper, IngredientMapper ingredientMapper, EntityManager entityManager, MenuItemViewEventRepository menuItemViewEventRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
        this.variantRepository = variantRepository;
        this.menuItemMapper = menuItemMapper;
        this.translatableMapper = translatableMapper;
        this.allergenMapper = allergenMapper;
        this.labelMapper = labelMapper;
        this.ingredientMapper = ingredientMapper;
        this.entityManager = entityManager;
        this.menuItemViewEventRepository = menuItemViewEventRepository;
    }

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
        Optional<Integer> maxDisplayOrder = menuItemRepository.findMaxDisplayOrder(menuItem.getCategoryId());
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
        Category oldCategory = findCategoryByMenuItemId(existingMenuItem.getId());
        updateMenuItem(existingMenuItem, menuItemFormDTO);
        switchCategory(existingMenuItem, oldCategory, menuItemFormDTO.categoryId());
        menuItemRepository.save(existingMenuItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#menuItemsDTOs.get(0).categoryId()")
    })
    public List<MenuItemSimpleDTO> updateDisplayOrders(List<MenuItemSimpleDTO> menuItemsDTOs, Long activeMenuId) {
        List<MenuItem> menuItems = menuItemsDTOs.stream().map(menuItemMapper::toMenuItem).toList();
        for (MenuItem menuItem : menuItems) {
            menuItemRepository.updateDisplayOrders(menuItem.getId(), menuItem.getDisplayOrder());
        }
        entityManager.clear();
        Long categoryId = menuItems.getFirst().getCategoryId();
        return getSimpleDTOs(categoryId);
    }

    @Override
    public List<MenuItemSimpleDTO> filterByName(String value) {
        String filterValue = "%" + value.toLowerCase() + "%";
        List<MenuItem> menuItems = menuItemRepository.filterByName(filterValue);
        return menuItems.stream().map(menuItemMapper::toDTO).toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#menuItemId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#menuItemId"),
            @CacheEvict(value = CATEGORY_ID, key = "#menuItemDTO.categoryId()")
    })
    public List<MenuItemSimpleDTO> delete(MenuItemSimpleDTO menuItemDTO, Long menuItemId) throws LocalizedException {
        MenuItem existingMenuItem = getMenuItem(menuItemDTO.id());
        removeVariants(existingMenuItem);
        Category category = findCategoryById(existingMenuItem.getCategoryId());
        removeMenuItem(category, existingMenuItem);
        List<MenuItem> menuItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(category.getId());
        sortingHelper.reassignDisplayOrders(menuItems, menuItemRepository::saveAllAndFlush);
        return getSimpleDTOs(category.getId());
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
                                Long newCategoryId) throws LocalizedException {
        if (Objects.isNull(existingMenuItem.getId())) {
            return;
        }
        if (oldCategory.getId().equals(newCategoryId)) {
            return;
        }
        Category newCategory = findCategoryById(newCategoryId);
        existingMenuItem = entityManager.merge(existingMenuItem);
        Optional<Integer> maxDisplayOrder = menuItemRepository.findMaxDisplayOrder(newCategoryId);
        existingMenuItem.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        newCategory.addMenuItem(existingMenuItem);
        categoryRepository.save(newCategory);

        List<MenuItem> oldCategoryItems = oldCategory.getMenuItems();
        oldCategoryItems.remove(existingMenuItem);
        sortingHelper.reassignDisplayOrders(oldCategoryItems, menuItemRepository::saveAll);
        categoryRepository.save(oldCategory);
    }

    private void updateMenuItem(MenuItem existingMenuItem, MenuItemFormDTO menuItemFormDTO) {
        existingMenuItem.setImageName(menuItemFormDTO.imageName());
        existingMenuItem.setName(translatableMapper.toTranslatable(menuItemFormDTO.name()));
        existingMenuItem.setDescription(translatableMapper.toTranslatable(menuItemFormDTO.description()));
        existingMenuItem.setCategoryId(menuItemFormDTO.categoryId());
        existingMenuItem.setPrice(menuItemFormDTO.price());
        existingMenuItem.setLabels(menuItemFormDTO.labels().stream()
                .map(labelMapper::toLabel).collect(Collectors.toSet()));
        existingMenuItem.setAllergens(menuItemFormDTO.allergens().stream()
                .map(allergenMapper::toAllergen).collect(Collectors.toSet()));
        existingMenuItem.setAdditionalIngredients(menuItemFormDTO.additionalIngredients().stream()
                .map(ingredientMapper::toIngredient).collect(Collectors.toSet()));
        existingMenuItem.setAvailable(menuItemFormDTO.available());
        existingMenuItem.setVisible(menuItemFormDTO.visible());
        existingMenuItem.setNew(menuItemFormDTO.isNew());
        existingMenuItem.setBestseller(menuItemFormDTO.isBestseller());
    }

    private List<MenuItemSimpleDTO> getSimpleDTOs(Long categoryId) {
        List<MenuItem> menuItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(categoryId);
        return menuItems.stream().map(menuItemMapper::toDTO).toList();
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
}