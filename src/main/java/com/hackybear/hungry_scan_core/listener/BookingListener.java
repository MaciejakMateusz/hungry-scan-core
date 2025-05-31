package com.hackybear.hungry_scan_core.listener;

import com.hackybear.hungry_scan_core.entity.Booking;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingListener {

    @PrePersist
    public void prePersist(final Booking booking) {
        booking.setExpirationTime(booking.getTime().plusHours(3L));
    }

}