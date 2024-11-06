package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface RestaurantTableService {

    List<RestaurantTable> findAll();

    RestaurantTable findById(Long id) throws LocalizedException;

    RestaurantTable findByNumber(Integer number) throws LocalizedException;

    RestaurantTable findByToken(String token) throws LocalizedException;

    void createNew(RestaurantTable restaurantTable) throws LocalizedException;

    void save(RestaurantTable restaurantTable);

    void delete(Long id) throws LocalizedException;

    RestaurantTable generateNewToken(Long id) throws LocalizedException;

    void toggleActivation(Long id) throws LocalizedException;

    void callWaiter(Long id) throws LocalizedException;

    void resolveWaiterCall(Long id) throws LocalizedException;

    void requestBill(Long id, PaymentMethod paymentMethod) throws LocalizedException;

}
