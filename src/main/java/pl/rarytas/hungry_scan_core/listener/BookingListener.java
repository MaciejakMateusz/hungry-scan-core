package pl.rarytas.hungry_scan_core.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.entity.Booking;

@Slf4j
@Getter
@Setter
public class BookingListener {

    @PrePersist
    public void prePersist(final Booking booking) {
        booking.setExpirationTime(booking.getTime().plusHours(3L));
    }

    @PostPersist
    public void postPersist(final Booking booking) {
        log.info("New booking received with ID: {}, date of order: {}, time of order: {}",
                booking.getId(),
                booking.getDate(), booking.getTime());
    }

    @PostUpdate
    public void postUpdate(final Booking booking) {
        log.info("Updated booking with ID: {}, date of order: {}, time of order: {}",
                booking.getId(),
                booking.getDate(),
                booking.getTime());
    }

    @PostRemove
    public void postRemove(final Booking booking) {
        log.info("Removed booking with ID: {}", booking.getId());
    }

}