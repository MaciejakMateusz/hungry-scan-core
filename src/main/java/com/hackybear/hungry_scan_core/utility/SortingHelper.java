package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingFunction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SortingHelper {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public SortingHelper(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public void sortAndSave(MenuItem menuItem, ThrowingFunction<Integer, MenuItem> findFunction) throws Exception {
        boolean isNew = isNewEntity(menuItem);
        MenuItem currentItem = getCurrentEntity(menuItem, findFunction, isNew, MenuItem.class);
        menuItem = persistIfNew(menuItem, menuItemRepository::save, isNew);

        Integer currentOrder = getCurrentOrder(currentItem);
        Integer newOrder = menuItem.getDisplayOrder();

        if (shouldSaveWithoutReordering(currentOrder, newOrder)) {
            menuItemRepository.save(menuItem);
            return;
        }

        List<MenuItem> categoryItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(menuItem.getCategory().getId());
        newOrder = adjustNewOrderIfNeeded(newOrder, categoryItems);

        if (isNew) {
            incrementDisplayOrders(categoryItems, newOrder);
        } else {
            adjustDisplayOrders(categoryItems, currentOrder, newOrder);
        }

        updateDisplayOrder(categoryItems, menuItem.getId(), newOrder);
        menuItemRepository.saveAll(categoryItems);
    }

    public void sortAndSave(Category category, ThrowingFunction<Integer, Category> findFunction) throws Exception {
        boolean isNew = isNewEntity(category);
        Category currentCategory = getCurrentEntity(category, findFunction, isNew, Category.class);
        category = persistIfNew(category, categoryRepository::save, isNew);

        Integer currentOrder = getCurrentOrder(currentCategory);
        Integer newOrder = category.getDisplayOrder();

        if (shouldSaveWithoutReordering(currentOrder, newOrder)) {
            categoryRepository.save(category);
            return;
        }

        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrder();
        newOrder = adjustNewOrderIfNeeded(newOrder, categories);

        if (isNew) {
            incrementDisplayOrders(categories, newOrder);
        } else {
            adjustDisplayOrders(categories, currentOrder, newOrder);
        }

        updateDisplayOrder(categories, category.getId(), newOrder);
        categoryRepository.saveAll(categories);
    }

    private <T> boolean isNewEntity(T entity) {
        return Objects.isNull(getId(entity));
    }

    private <T> T getCurrentEntity(T entity, ThrowingFunction<Integer, T> function, boolean isNew, Class<T> clazz) throws Exception {
        if (!isNew) {
            return function.apply(getId(entity));
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create a new instance of class: " + clazz.getName(), e);
        }
    }

    private <T> T persistIfNew(T entity, ThrowingFunction<T, T> saveFunction, boolean isNew) throws Exception {
        if (isNew) {
            return saveFunction.apply(entity);
        }
        return entity;
    }

    private <T> Integer getCurrentOrder(T currentEntity) {
        return Objects.isNull(getDisplayOrder(currentEntity)) ? 0 : getDisplayOrder(currentEntity);
    }

    private boolean shouldSaveWithoutReordering(Integer currentOrder, Integer newOrder) {
        return currentOrder.equals(newOrder) && newOrder != 0;
    }

    private <T> Integer adjustNewOrderIfNeeded(Integer newOrder, List<T> collection) {
        if (collection.size() == 1) {
            return 1;
        }

        int maxOrder = collection.size();
        if (newOrder > maxOrder) {
            return maxOrder;
        } else if (newOrder < 1) {
            return 1;
        }
        return newOrder;
    }

    private <T> void incrementDisplayOrders(List<T> collection, Integer newOrder) {
        for (T t : collection) {
            if (getDisplayOrder(t) >= newOrder) {
                setDisplayOrder(t, getDisplayOrder(t) + 1);
            }
        }
    }

    private <T> void adjustDisplayOrders(List<T> collection, Integer currentOrder, Integer newOrder) {
        if (newOrder > currentOrder) {
            for (T t : collection) {
                if (getDisplayOrder(t) > currentOrder && getDisplayOrder(t) <= newOrder) {
                    setDisplayOrder(t, getDisplayOrder(t) - 1);
                }
            }
        } else {
            for (T t : collection) {
                if (getDisplayOrder(t) >= newOrder && getDisplayOrder(t) < currentOrder) {
                    setDisplayOrder(t, getDisplayOrder(t) + 1);
                }
            }
        }
    }

    private <T> void updateDisplayOrder(List<T> collection, Integer entityId, Integer newOrder) {
        for (T t : collection) {
            if (getId(t).equals(entityId)) {
                setDisplayOrder(t, newOrder);
            }
        }
    }

    private Integer getDisplayOrder(Object obj) {
        if (obj instanceof MenuItem) {
            return ((MenuItem) obj).getDisplayOrder();
        } else if (obj instanceof Category) {
            return ((Category) obj).getDisplayOrder();
        }
        throw new IllegalArgumentException("Unsupported type");
    }

    private void setDisplayOrder(Object obj, Integer displayOrder) {
        if (obj instanceof MenuItem) {
            ((MenuItem) obj).setDisplayOrder(displayOrder);
        } else if (obj instanceof Category) {
            ((Category) obj).setDisplayOrder(displayOrder);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    private Integer getId(Object obj) {
        if (obj instanceof MenuItem) {
            return ((MenuItem) obj).getId();
        } else if (obj instanceof Category) {
            return ((Category) obj).getId();
        }
        throw new IllegalArgumentException("Unsupported type");
    }
}
