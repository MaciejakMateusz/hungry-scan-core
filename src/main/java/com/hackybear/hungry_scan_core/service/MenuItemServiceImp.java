package com.hackybear.hungry_scan_core.service;

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

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MenuItemServiceImp implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final VariantRepository variantRepository;

    public MenuItemServiceImp(MenuItemRepository menuItemRepository,
                              CategoryRepository categoryRepository,
                              ExceptionHelper exceptionHelper,
                              SortingHelper sortingHelper,
                              VariantRepository variantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
        this.variantRepository = variantRepository;
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem findById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void save(MenuItem menuItem) throws Exception {
        switchCategory(menuItem);
        sortingHelper.sortAndSave(menuItem, this::findById);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        MenuItem existingMenuItem = findById(id);
        Category category = findByMenuItem(existingMenuItem);
        List<Variant> variants = variantRepository.findAllByMenuItemOrderByDisplayOrder(existingMenuItem);
        variantRepository.deleteAll(variants);
        category.removeMenuItem(existingMenuItem);
        menuItemRepository.delete(existingMenuItem);
        sortingHelper.updateDisplayOrders(existingMenuItem.getDisplayOrder(), category.getMenuItems(), menuItemRepository::saveAll);
    }

    private Category findByMenuItem(MenuItem menuItem) throws LocalizedException {
        return categoryRepository.findByMenuItem(menuItem)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFoundByMenuItem", menuItem.getId()));
    }

    private Category findCategoryById(Integer id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    private void switchCategory(MenuItem menuItem) throws LocalizedException {
        if (Objects.isNull(menuItem.getId())) {
            return;
        }
        Category category = findByMenuItem(menuItem);
        if (category.getId().equals(menuItem.getCategoryId())) {
            return;
        }
        category.removeMenuItem(menuItem);
        sortingHelper.updateDisplayOrders(menuItem.getDisplayOrder(), category.getMenuItems(), menuItemRepository::saveAll);

        Category newCategory = findCategoryById(menuItem.getCategoryId());
        newCategory.addMenuItem(menuItem);
        categoryRepository.save(newCategory);
    }

}