package pl.rarytas.rarytas_restaurantside.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.enums.DayPart;
import pl.rarytas.rarytas_restaurantside.enums.OpeningHours;

import java.time.LocalTime;

@Slf4j
@Getter
@Setter
public class BookingListener {

    private static final int OPENING = OpeningHours.OPENING.getIntValue();
    private static final int CLOSING = OpeningHours.CLOSING.getIntValue();
    private static final LocalTime FIRST_HALF_START = LocalTime.of(OPENING, 0);
    private static final LocalTime FIRST_HALF_END = LocalTime.of(CLOSING / 2, 0);
    private static final LocalTime SECOND_HALF_START = LocalTime.of(CLOSING / 2, 1);
    private static final LocalTime SECOND_HALF_END = LocalTime.of(CLOSING, 0);

    @PrePersist
    private void prePersist(final Booking booking) {
        calculateDayPart(booking);
    }

    public void calculateDayPart(Booking booking) {
        if (booking.getTime().isAfter(FIRST_HALF_START) && booking.getTime().isBefore(FIRST_HALF_END)) {
            booking.setDayPart(DayPart.FIRST_HALF);
        } else if (booking.getTime().isAfter(SECOND_HALF_START) && booking.getTime().isBefore(SECOND_HALF_END)) {
            booking.setDayPart(DayPart.SECOND_HALF);
        }
    }

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