package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class SortingHelper {

    public <T> void reassignDisplayOrders(Set<T> collection, Consumer<Set<T>> consumer) {
        int i = 1;
        for (T t : collection) {
            setDisplayOrder(t, i++);
        }
        consumer.accept(collection);
    }

    public <T> void reassignDisplayOrders(List<T> collection, Consumer<List<T>> consumer) {
        for (int i = 0; i <= collection.size() - 1; i++) {
            T t = collection.get(i);
            setDisplayOrder(t, i + 1);
        }
        consumer.accept(collection);
    }

    private void setDisplayOrder(Object obj, Integer displayOrder) {
        switch (obj) {
            case MenuItem menuItem -> menuItem.setDisplayOrder(displayOrder);
            case Category category -> category.setDisplayOrder(displayOrder);
            case Variant variant -> variant.setDisplayOrder(displayOrder);
            case null, default -> throw new IllegalArgumentException("Unsupported type");
        }
    }
}
