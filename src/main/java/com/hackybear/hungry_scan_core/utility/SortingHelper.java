package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class SortingHelper {

    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;

    public SortingHelper(MenuItemRepository menuItemRepository,
                         VariantRepository variantRepository,
                         ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
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

    public <T> void reassignDisplayOrders(List<T> collection, Consumer<List<T>> consumer) {
        for (int i = 0; i <= collection.size() - 1; i++) {
            T t = collection.get(i);
            setDisplayOrder(t, i + 1);
        }
        consumer.accept(collection);
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
