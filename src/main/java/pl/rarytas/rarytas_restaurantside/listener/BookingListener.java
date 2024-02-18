package pl.rarytas.rarytas_restaurantside.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.Booking;

@Slf4j
@Getter
@Setter
public class BookingListener {

    @PostPersist
    public void postPersist(final Booking booking) {
        log.info("New booking received with ID: "
                + booking.getId() + ", date of order: "
                + booking.getDate() + ", time of order: "
                + booking.getTime());
    }

    @PostUpdate
    public void postUpdate(final Booking booking) {
        log.info("Updated booking with ID: " + booking.getId() +
                ", date of order: " + booking.getDate() +
                ", time of order: " + booking.getTime());
    }

    @PostRemove
    public void postRemove(final Booking booking) {
        log.info("Removed booking with ID: " + booking.getId());
    }
}