package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.BookingRepository;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final SettingsService settingsService;

    public boolean isValidBooking(Booking booking) throws LocalizedException {
        return DateTimeHelper.isNotInPast(LocalDateTime.of(booking.getDate(), booking.getTime())) &&
                !hasBookingCollision(booking) &&
                isWithinOpeningHours(booking);
    }

    private boolean hasBookingCollision(Booking booking) {
        for (RestaurantTable restaurantTable : booking.getRestaurantTables()) {
            List<Booking> existingBookings =
                    bookingRepository.findAllByRestaurantTablesId(restaurantTable.getId());
            boolean hasCollision = existingBookings.stream()
                    .anyMatch(existingBooking -> {
                        try {
                            return existingBooking.getDate().equals(booking.getDate()) &&
                                    (existingBooking.getTime().equals(booking.getTime()) ||
                                            isInBookingTimeRange(booking, existingBooking) ||
                                            bookingIntersects(booking, existingBooking));
                        } catch (LocalizedException e) {
                            throw new RuntimeException(e);
                        }
                    });
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

    private boolean bookingIntersects(Booking booking, Booking existingBooking) throws LocalizedException {
        return DateTimeHelper.doTimeRangesOverlap(
                booking.getTime().plusHours(getSettings().bookingDuration()),
                existingBooking.getTime(),
                existingBooking.getExpirationTime());
    }

    private boolean isWithinOpeningHours(Booking booking) throws LocalizedException {
        return booking.getTime().isAfter(getSettings().openingTime()) &&
                booking.getTime().isBefore(getSettings().closingTime());
    }

    private SettingsDTO getSettings() throws LocalizedException {
        return settingsService.getSettings();
    }
}