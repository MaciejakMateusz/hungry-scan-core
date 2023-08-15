package pl.rarytas.rarytas_restaurantside.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.Order;

@Slf4j
@Getter
@Setter
public class OrderListener {

    @PostPersist
    public void postPersist(final Order order) {
        log.info("New order received with ID: " + order.getId() + ", time of order: " + order.getOrderTime());
    }

    @PostUpdate
    public void postUpdate(Order order) {
        log.info("Updated order with ID: " + order.getId() + ", time of order: " + order.getOrderTime());
    }

    @PostRemove
    public void postRemove(Order order) {
        log.info("Removed order with ID: " + order.getId());
    }
}