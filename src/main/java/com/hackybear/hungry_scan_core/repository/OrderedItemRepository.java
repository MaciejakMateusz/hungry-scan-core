package com.hackybear.hungry_scan_core.repository;

import org.springframework.stereotype.Repository;
import com.hackybear.hungry_scan_core.entity.OrderedItem;

@Repository
public interface OrderedItemRepository extends CustomRepository<OrderedItem, Long> {
}
