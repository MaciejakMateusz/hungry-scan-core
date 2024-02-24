package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {
    List<RestaurantTable> findAll();

    Optional<RestaurantTable> findById(Integer id);

    void bookTable(Booking booking) throws LocalizedException;

    void removeBooking(Booking booking);
}
