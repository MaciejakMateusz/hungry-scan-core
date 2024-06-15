package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingFunction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class SortingHelper {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;

    public SortingHelper(MenuItemRepository menuItemRepository,
                         CategoryRepository categoryRepository,
                         VariantRepository variantRepository,
                         ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
    }

    public void sortAndSave(MenuItem menuItem, ThrowingFunction<Integer, MenuItem> findFunction) throws Exception {
        boolean isNew = isNewEntity(menuItem);
        MenuItem currentItem = getCurrentEntity(menuItem, findFunction, isNew, MenuItem.class);
        Integer currentOrder = getCurrentOrder(currentItem);
        Integer newOrder = getNewOrder(menuItem.getDisplayOrder());

        if (shouldSaveWithoutReordering(currentOrder, menuItem, menuItemRepository::save)) {
            return;
        }

        Category category = categoryRepository.findById(menuItem.getCategoryId())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", menuItem.getCategoryId()));

        if (isNew) {
            menuItemRepository.save(menuItem);
            category.addMenuItem(menuItem);
            categoryRepository.save(category);
        }

        List<MenuItem> categoryItems = category.getMenuItems();
        newOrder = adjustNewOrderIfNeeded(newOrder, categoryItems);

        if (isNew) {
            incrementDisplayOrders(categoryItems, newOrder);
        } else {
            adjustDisplayOrders(categoryItems, currentOrder, newOrder);
        }

        updateDisplayOrder(categoryItems, menuItem, newOrder);
        menuItemRepository.saveAll(categoryItems);
    }

    public void sortAndSave(Category category, ThrowingFunction<Integer, Category> findFunction) throws Exception {
        boolean isNew = isNewEntity(category);
        Category currentCategory = getCurrentEntity(category, findFunction, isNew, Category.class);
        Integer currentOrder = getCurrentOrder(currentCategory);
        Integer newOrder = getNewOrder(category.getDisplayOrder());

        if (shouldSaveWithoutReordering(currentOrder, category, categoryRepository::save)) {
            return;
        }

        category = persistIfNew(category, categoryRepository::save, isNew);

        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrder();
        newOrder = adjustNewOrderIfNeeded(newOrder, categories);

        if (isNew) {
            incrementDisplayOrders(categories, newOrder);
        } else {
            adjustDisplayOrders(categories, currentOrder, newOrder);
        }

        updateDisplayOrder(categories, category, newOrder);
        categoryRepository.saveAll(categories);
    }

    private Integer getNewOrder(Integer displayOrder) {
        return Objects.isNull(displayOrder) ? 1 : displayOrder;
    }

    public void sortAndSave(Variant variant, ThrowingFunction<Integer, Variant> findFunction) throws Exception {
        boolean isNew = isNewEntity(variant);
        Variant currentVariant = getCurrentEntity(variant, findFunction, isNew, Variant.class);
        Integer currentOrder = getCurrentOrder(currentVariant);
        Integer newOrder = getNewOrder(variant.getDisplayOrder());

        if (shouldSaveWithoutReordering(currentOrder, variant, variantRepository::save)) {
            return;
        }

        variant = persistIfNew(variant, variantRepository::save, isNew);

        List<Variant> variants = variantRepository.findAllByMenuItemIdOrderByDisplayOrder(variant.getMenuItem().getId());
        newOrder = adjustNewOrderIfNeeded(newOrder, variants);

        if (isNew) {
            incrementDisplayOrders(variants, newOrder);
        } else {
            adjustDisplayOrders(variants, currentOrder, newOrder);
        }

        updateDisplayOrder(variants, variant, newOrder);
        variantRepository.saveAll(variants);
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

    private <T> boolean shouldSaveWithoutReordering(Integer currentOrder, T t, Consumer<T> save) {
        if (currentOrder.equals(getDisplayOrder(t)) && getDisplayOrder(t) != 0) {
            save.accept(t);
            return true;
        }
        return false;
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
        if (collection.size() == 1) {
            return;
        }
        for (T t : collection) {
            if (getDisplayOrder(t) >= newOrder) {
                setDisplayOrder(t, getDisplayOrder(t) + 1);
            }
        }
    }

    private <T> void adjustDisplayOrders(List<T> collection, Integer currentOrder, Integer newOrder) {
        if (collection.size() == 1) {
            return;
        }
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

    public <T> void updateDisplayOrders(Integer removedDisplayOrder,
                                        List<T> collection,
                                        Consumer<List<T>> consumer) {
        for (T t : collection) {
            Integer currentDisplayOrder = getDisplayOrder(t);
            if (removedDisplayOrder <= currentDisplayOrder) {
                setDisplayOrder(t, currentDisplayOrder - 1);
            }
        }
        consumer.accept(collection);
    }

    private <T> void updateDisplayOrder(List<T> collection, T entity, Integer newOrder) {
        for (int i = 0; i < collection.size(); i++) {
            T t = collection.get(i);
            if (getId(t).equals(getId(entity))) {
                setDisplayOrder(entity, newOrder);
                collection.set(i, entity);
                break;
            }
        }
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

    private Integer getId(Object obj) {
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
