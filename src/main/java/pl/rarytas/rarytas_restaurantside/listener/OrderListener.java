package pl.rarytas.rarytas_restaurantside.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@Setter
public class OrderListener {

    private Integer orderCounter = 0;
    private LocalDate lastOrderDate = null;

    @PrePersist
    private void prePersist(final Order order) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        String nowString = dtf.format(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        if (lastOrderDate == null || now.toLocalDate().isAfter(lastOrderDate)) {
            LocalTime noon = LocalTime.of(12, 0); // 12:00 pm
            if (now.toLocalTime().isAfter(noon)) {
                orderCounter = 0;
            }
            lastOrderDate = now.toLocalDate();
        }
        order.setOrderTime(nowString);
        orderCounter++;
        order.setOrderNumber(orderCounter);
    }

    @PostPersist
    public void postPersist(final Order order) {
        log.info("New order received with ID: " + order.getId() + ", time of order: " + order.getOrderTime());
    }

    @PostUpdate
    public void postUpdate(final Order order) {
        log.info("Updated order with ID: " + order.getId() + ", time of order: " + order.getOrderTime());
    }

    @PostRemove
    public void postRemove(final Order order) {
        log.info("Removed order with ID: " + order.getId());
    }
}