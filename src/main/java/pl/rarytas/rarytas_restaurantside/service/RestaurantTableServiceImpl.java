package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final BookingService bookingService;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository, BookingService bookingService) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.bookingService = bookingService;
    }


    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public Optional<RestaurantTable> findById(Integer id) {
        return restaurantTableRepository.findById(id);
    }

    @Override
    @Transactional
    public void bookTable(Booking booking) throws LocalizedException {
        bookingService.save(booking);
    }

    @Override
    public void removeBooking(Booking booking) {
        RestaurantTable table = findById(booking.getTableId()).orElseThrow();
        table.getBookings().remove(booking);
        restaurantTableRepository.saveAndFlush(table);
    }
}
