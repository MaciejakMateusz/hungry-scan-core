package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.OrderedItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderedItemRepository extends CustomRepository<OrderedItem, Long> {

    @Query("SELECT i FROM OrderedItem i WHERE i.menuItem.barServed = true")
    List<OrderedItem> findAllDrinks();
}
