package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.RestaurantTable;
import pl.rarytas.hungry_scan_core.enums.PaymentMethod;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface RestaurantTableService {

    List<RestaurantTable> findAll();

    RestaurantTable findById(Integer id) throws LocalizedException;

    RestaurantTable findByNumber(Integer number) throws LocalizedException;

    RestaurantTable findByToken(String token) throws LocalizedException;

    void createNew(RestaurantTable restaurantTable) throws LocalizedException;

    void save(RestaurantTable restaurantTable);

    void delete(Integer id) throws LocalizedException;

    RestaurantTable generateNewToken(Integer id) throws LocalizedException;

    void toggleActivation(Integer id) throws LocalizedException;

    void callWaiter(Integer id) throws LocalizedException;

    void resolveWaiterCall(Integer id) throws LocalizedException;

    void requestBill(Integer id, PaymentMethod paymentMethod) throws LocalizedException;

}
