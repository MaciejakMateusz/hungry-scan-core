package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class SortingHelper {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final MenuRepository menuRepository;
    private final ExceptionHelper exceptionHelper;

    public SortingHelper(MenuItemRepository menuItemRepository,
                         CategoryRepository categoryRepository,
                         VariantRepository variantRepository,
                         MenuRepository menuRepository, ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;
        this.menuRepository = menuRepository;
        this.exceptionHelper = exceptionHelper;
    }

    public void sortAndSave(MenuItem menuItem, ThrowingFunction<Long, MenuItem> findFunction) throws Exception {
        boolean isNew = isNewEntity(menuItem);
        MenuItem currentItem = getCurrentEntity(menuItem, findFunction, isNew, MenuItem.class);
        int newOrder = getNewOrder(menuItem.getDisplayOrder());

        Long categoryId = menuItem.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", categoryId));

        List<MenuItem> categoryItems = category.getMenuItems()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        if (!isNew) {
            categoryItems.remove(currentItem);
            updateDisplayOrdersAfterRemoval(categoryItems);
        }

        adjustDisplayOrdersForInsertion(menuItem, categoryItems, newOrder);

        category.setMenuItems(categoryItems);
        categoryRepository.save(category);
        menuItemRepository.saveAll(categoryItems);
    }

    public void sortAndSave(Category category, ThrowingFunction<Long, Category> findFunction) throws Exception {
        boolean isNew = isNewEntity(category);
        Category currentCategory = getCurrentEntity(category, findFunction, isNew, Category.class);
        int newOrder = getNewOrder(category.getDisplayOrder());

        Long menuId = category.getMenuId();
        List<Category> menuCategories = categoryRepository.findAllByMenuIdOrderByDisplayOrder(menuId);

        if (!isNew) {
            menuCategories.remove(currentCategory);
            updateDisplayOrdersAfterRemoval(menuCategories);
        }

        adjustDisplayOrdersForInsertion(category, menuCategories, newOrder);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound", menuId));
        menu.setCategories(menuCategories);
        categoryRepository.save(category);
        menuRepository.save(menu);
    }

    private Integer getNewOrder(Integer displayOrder) {
        return Objects.isNull(displayOrder) ? 1 : displayOrder;
    }

    public void sortAndSave(Variant variant, ThrowingFunction<Long, Variant> findFunction) throws Exception {
        boolean isNew = isNewEntity(variant);
        Variant currentItem = getCurrentEntity(variant, findFunction, isNew, Variant.class);
        int newOrder = getNewOrder(variant.getDisplayOrder());

        MenuItem menuItem = getMenuItem(variant.getMenuItemId());
        List<Variant> menuItemVariants = variantRepository.findAllByMenuItemIdOrderByDisplayOrder(variant.getMenuItemId());

        if (!isNew) {
            menuItemVariants.remove(currentItem);
            updateDisplayOrdersAfterRemoval(menuItemVariants);
        }

        adjustDisplayOrdersForInsertion(variant, menuItemVariants, newOrder);

        menuItem.setVariants(new HashSet<>(menuItemVariants));
        menuItemRepository.save(menuItem);
    }

    public void removeAndAdjust(MenuItem menuItem) throws LocalizedException {
        Long categoryId = menuItem.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        exceptionHelper.supplyLocalizedMessage(
                                "error.categoryService.categoryNotFound", categoryId));

        List<MenuItem> categoryItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(categoryId);

        categoryItems.remove(menuItem);
        updateDisplayOrdersAfterRemoval(categoryItems);

        category.setMenuItems(categoryItems);
        categoryRepository.save(category);
        menuItemRepository.delete(menuItem);
    }

    public void removeAndAdjust(Category category) throws LocalizedException {
        Long menuId = category.getMenuId();
        List<Category> menuCategories = categoryRepository.findAllByMenuIdOrderByDisplayOrder(menuId);

        menuCategories.remove(category);
        updateDisplayOrdersAfterRemoval(menuCategories);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound", menuId));
        menu.setCategories(menuCategories);
        menuRepository.save(menu);
        categoryRepository.delete(category);
    }

    public void removeAndAdjust(Variant variant) throws LocalizedException {
        Long menuItemId = variant.getMenuItemId();
        MenuItem menuItem = getMenuItem(menuItemId);
        menuItem.removeVariant(variant);
        List<Variant> menuItemVariants = new ArrayList<>(menuItem.getVariants().stream().sorted().toList());

        updateDisplayOrdersAfterRemoval(menuItemVariants);
        menuItem.setVariants(new HashSet<>(menuItemVariants));
        menuItemRepository.save(menuItem);
        variantRepository.delete(variant);
    }

    private <T extends Comparable<T>> void updateDisplayOrdersAfterRemoval(List<T> collection) {
        for (int i = 0; i < collection.size(); i++) {
            setDisplayOrder(collection.get(i), i + 1);
        }
    }

    private <T extends Comparable<T>> void renumberDisplayOrders(List<T> collection) {
        int order = 1;
        for (T item : collection) {
            setDisplayOrder(item, order++);
        }
    }

    private <T extends Comparable<T>> void adjustDisplayOrdersForInsertion(T newItem, List<T> collection, int newOrder) {
        if (newOrder < 1) {
            newOrder = 1;
        } else if (newOrder > collection.size()) {
            newOrder = collection.size() + 1;
        }

        collection.add(newOrder - 1, newItem);
        renumberDisplayOrders(collection);
    }

    private <T> boolean isNewEntity(T entity) {
        return Objects.isNull(getId(entity));
    }

    private <T> T getCurrentEntity(T entity, ThrowingFunction<Long, T> function, boolean isNew, Class<T> clazz) throws Exception {
        if (!isNew) {
            return function.apply(getId(entity));
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create a new instance of class: " + clazz.getName(), e);
        }
    }

    private MenuItem getMenuItem(Long id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    public <T> void updateDisplayOrders(Integer removedDisplayOrder,
                                        List<T> collection,
                                        Consumer<List<T>> consumer) {
        for (T t : collection) {
            Integer currentDisplayOrder = getDisplayOrder(t);
            if (removedDisplayOrder < currentDisplayOrder) {
                setDisplayOrder(t, currentDisplayOrder - 1);
            }
        }
        consumer.accept(collection);
    }

    private Integer getDisplayOrder(Object obj) {
        if (obj instanceof MenuItem) {
            return ((MenuItem) obj).getDisplayOrder();
        } else if (obj instanceof Category) {
            return ((Category) obj).getDisplayOrder();
        } else if (obj instanceof Variant) {
            return ((Variant) obj).getDisplayOrder();
        }
        throw new IllegalArgumentException("Unsupported type");
    }

    private void setDisplayOrder(Object obj, Integer displayOrder) {
        if (obj instanceof MenuItem) {
            ((MenuItem) obj).setDisplayOrder(displayOrder);
        } else if (obj instanceof Category) {
            ((Category) obj).setDisplayOrder(displayOrder);
        } else if (obj instanceof Variant) {
            ((Variant) obj).setDisplayOrder(displayOrder);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    private Long getId(Object obj) {
        if (obj instanceof MenuItem) {
            return ((MenuItem) obj).getId();
        } else if (obj instanceof Category) {
            return ((Category) obj).getId();
        } else if (obj instanceof Variant) {
            return ((Variant) obj).getId();
        }
        throw new IllegalArgumentException("Unsupported type");
    }
}
