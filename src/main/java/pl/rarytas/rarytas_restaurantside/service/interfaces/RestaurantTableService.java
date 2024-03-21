package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface RestaurantTableService {

    List<RestaurantTable> findAll();

    RestaurantTable findById(Integer id) throws LocalizedException;

    RestaurantTable findByToken(String token) throws LocalizedException;

    void save(RestaurantTable restaurantTable);

    void toggleActivation(Integer id) throws LocalizedException;

    void bookTable(Booking booking) throws LocalizedException;

    void removeBooking(Booking booking) throws LocalizedException;
}
