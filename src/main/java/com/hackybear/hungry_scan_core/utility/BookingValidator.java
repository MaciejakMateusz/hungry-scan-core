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
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final BookingRepository bookingRepository;
    private final SettingsService settingsService;

    public boolean isValidBooking(Booking booking) throws LocalizedException {
        return DateTimeHelper.isNotInPast(LocalDateTime.of(booking.getDate(), booking.getTime())) &&
                !hasBookingCollision(booking) &&
                isWithinOperatingHours(booking);
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

    private boolean isWithinOperatingHours(Booking booking) throws LocalizedException {
        LocalTime bookingTime = booking.getTime();

        return getSettings().operatingHours().values().stream().allMatch(timeRange -> {
            LocalTime start = timeRange.getStartTime();
            LocalTime end = timeRange.getEndTime();

            if (end.isAfter(start)) {
                return !bookingTime.isBefore(start) && bookingTime.isBefore(end);
            } else {
                return !bookingTime.isBefore(start) || bookingTime.isBefore(end);
            }
        });
    }

    private SettingsDTO getSettings() throws LocalizedException {
        return settingsService.getSettings();
    }
}