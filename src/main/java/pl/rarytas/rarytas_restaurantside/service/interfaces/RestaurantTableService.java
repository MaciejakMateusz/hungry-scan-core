package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface RestaurantTableService {

    List<RestaurantTable> findAll();

    RestaurantTable findById(Integer id) throws LocalizedException;

    RestaurantTable findByNumber(Integer number) throws LocalizedException;

    RestaurantTable findByToken(String token) throws LocalizedException;

    void createNew(RestaurantTable restaurantTable) throws LocalizedException;

    void save(RestaurantTable restaurantTable);

    void delete(Integer id) throws LocalizedException;

    void toggleActivation(Integer id) throws LocalizedException;

    void callWaiter(Integer id) throws LocalizedException;

    void resolveWaiterCall(Integer id) throws LocalizedException;

    void requestBill(Integer id, PaymentMethod paymentMethod) throws LocalizedException;

}
