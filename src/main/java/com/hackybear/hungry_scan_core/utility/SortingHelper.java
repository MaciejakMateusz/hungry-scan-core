package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class SortingHelper {

    public <T> void reassignDisplayOrders(List<T> collection, Consumer<List<T>> consumer) {
        for (int i = 0; i <= collection.size() - 1; i++) {
            T t = collection.get(i);
            setDisplayOrder(t, i + 1);
        }
        consumer.accept(collection);
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
}
