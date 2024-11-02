package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.*;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public MenuItemServiceImp(MenuItemRepository menuItemRepository,
                              CategoryRepository categoryRepository,
                              ExceptionHelper exceptionHelper,
                              SortingHelper sortingHelper,
                              VariantRepository variantRepository, MenuItemMapper menuItemMapper, TranslatableMapper translatableMapper, AllergenMapper allergenMapper, LabelMapper labelMapper, IngredientMapper ingredientMapper) {
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
    }

    @Override
    public MenuItemFormDTO findById(Long id) throws LocalizedException {
        MenuItem menuItem = getMenuItem(id);
        return menuItemMapper.toFormDTO(menuItem);
    }

    @Override
    @Transactional
    public void save(MenuItemFormDTO menuItemFormDTO) throws Exception {
        MenuItem menuItem = menuItemMapper.toMenuItem(menuItemFormDTO);
        sortingHelper.sortAndSave(menuItem, this::getMenuItem);
    }

    @Override
    @Transactional
    public void update(MenuItemFormDTO menuItemFormDTO) throws Exception {
        MenuItem existingMenuItem = getMenuItem(menuItemFormDTO.id());
        Category category = findByMenuItemId(existingMenuItem.getId());
        updateMenuItem(existingMenuItem, menuItemFormDTO);
        switchCategory(existingMenuItem, category);
        sortingHelper.sortAndSave(existingMenuItem, this::getMenuItem);
    }

    @Override
    public List<MenuItemSimpleDTO> filterByName(String value) {
        String filterValue = "%" + value.toLowerCase() + "%";
        List<MenuItem> menuItems = menuItemRepository.filterByName(filterValue);
        return menuItems.stream().map(menuItemMapper::toDTO).toList();
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        MenuItem existingMenuItem = getMenuItem(id);
        List<Variant> variants = variantRepository.findAllByMenuItemIdOrderByDisplayOrder(id);
        variantRepository.deleteAll(variants);
        sortingHelper.removeAndAdjust(existingMenuItem);
    }

    private MenuItem getMenuItem(Long id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    private Category findByMenuItemId(Long id) throws LocalizedException {
        return categoryRepository.findByMenuItemId(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFoundByMenuItem", id));
    }

    private Category findCategoryById(Long id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    private void switchCategory(MenuItem menuItem, Category category) throws LocalizedException {
        if (Objects.isNull(menuItem.getId())) {
            return;
        }
        Long categoryId = menuItem.getCategoryId();
        if (category.getId().equals(categoryId)) {
            return;
        }
        category.removeMenuItem(menuItem);
        sortingHelper.updateDisplayOrders(menuItem.getDisplayOrder(), category.getMenuItems(), menuItemRepository::saveAll);

        Category newCategory = findCategoryById(categoryId);
        newCategory.addMenuItem(menuItem);
        categoryRepository.save(newCategory);
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
        existingMenuItem.setDisplayOrder(menuItemFormDTO.displayOrder());
        existingMenuItem.setAvailable(menuItemFormDTO.available());
        existingMenuItem.setVisible(menuItemFormDTO.visible());
        existingMenuItem.setNew(menuItemFormDTO.isNew());
        existingMenuItem.setBestseller(menuItemFormDTO.isBestseller());
    }

}