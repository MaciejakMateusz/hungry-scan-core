package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface OrderedItemService {
    List<OrderedItem> findAll();

    List<OrderedItem> findAllDrinks();

    OrderedItem findById(Long id) throws LocalizedException;

}
