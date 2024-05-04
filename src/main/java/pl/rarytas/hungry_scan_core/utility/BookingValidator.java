package pl.rarytas.hungry_scan_core.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.hungry_scan_core.entity.Booking;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;
import pl.rarytas.hungry_scan_core.entity.Settings;
import pl.rarytas.hungry_scan_core.repository.BookingRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.SettingsService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final SettingsService settingsService;

    public BookingValidator(BookingRepository bookingRepository, SettingsService settingsService) {
        this.bookingRepository = bookingRepository;
        this.settingsService = settingsService;
    }

    public boolean isValidBooking(Booking booking) {
        return DateTimeHelper.isNotInPast(LocalDateTime.of(booking.getDate(), booking.getTime())) &&
                !hasBookingCollision(booking) &&
                isWithinOpeningHours(booking);
    }

    private boolean hasBookingCollision(Booking booking) {
        for (RestaurantTable restaurantTable : booking.getRestaurantTables()) {
            List<Booking> existingBookings =
                    bookingRepository.findAllByRestaurantTablesId(restaurantTable.getId());
            boolean hasCollision = existingBookings.stream()
                    .anyMatch(existingBooking -> existingBooking.getDate().equals(booking.getDate()) &&
                            (existingBooking.getTime().equals(booking.getTime()) ||
                                    isInBookingTimeRange(booking, existingBooking) ||
                                    bookingIntersects(booking, existingBooking)));
            if (hasCollision) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBookingTimeRange(Booking booking, Booking existingBooking) {
        return DateTimeHelper.isInTimeRange(
                booking.getTime(),
                existingBooking.getTime(),
                existingBooking.getExpirationTime());
    }

    private boolean bookingIntersects(Booking booking, Booking existingBooking) {
        return DateTimeHelper.doTimeRangesOverlap(
                booking.getTime().plusHours(getSettings().getBookingDuration()),
                existingBooking.getTime(),
                existingBooking.getExpirationTime());
    }

    private boolean isWithinOpeningHours(Booking booking) {
        return booking.getTime().isAfter(getSettings().getOpeningTime()) &&
                booking.getTime().isBefore(getSettings().getClosingTime());
    }

    private Settings getSettings() {
        return settingsService.getSettings();
    }
}