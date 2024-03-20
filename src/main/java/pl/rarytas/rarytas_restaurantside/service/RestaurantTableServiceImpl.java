package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;

@Slf4j
@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final BookingService bookingService;
    private final ExceptionHelper exceptionHelper;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository, BookingService bookingService, ExceptionHelper exceptionHelper) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.bookingService = bookingService;
        this.exceptionHelper = exceptionHelper;
    }


    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable findById(Integer id) throws LocalizedException {
        return restaurantTableRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantTableService.tableNotFound", id));
    }

    @Override
    public RestaurantTable findByToken(String token) throws LocalizedException {
        return restaurantTableRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.general.accessDenied"));
    }

    @Override
    public void save(RestaurantTable restaurantTable) {
        restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public void toggleActivation(Integer id) {
        RestaurantTable table;
        try {
            table = findById(id);
        } catch (LocalizedException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
        table.setActive(!table.isActive());
        save(table);
    }

    @Override
    @Transactional
    public void bookTable(Booking booking) throws LocalizedException {
        bookingService.save(booking);
    }

    @Override
    public void removeBooking(Booking booking) {
        RestaurantTable table;
        try {
            table = findById(booking.getTableId());
        } catch (LocalizedException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
        table.getBookings().remove(booking);
        restaurantTableRepository.saveAndFlush(table);
    }
}
