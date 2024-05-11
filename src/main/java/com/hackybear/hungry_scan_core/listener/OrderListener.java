package com.hackybear.hungry_scan_core.listener;

import com.hackybear.hungry_scan_core.entity.Order;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
public class OrderListener {

    @PrePersist
    private void prePersist(final Order order) {
        LocalDateTime now = LocalDateTime.now();
        order.setOrderTime(now);
    }

    @PostPersist
    public void postPersist(final Order order) {
        log.info("New order received with ID: {}, time of order: {}", order.getId(), order.getOrderTime());
    }

    @PostUpdate
    public void postUpdate(final Order order) {
        log.info("Updated order with ID: {}, time of order: {}", order.getId(), order.getOrderTime());
    }

    @PostRemove
    public void postRemove(final Order order) {
        log.info("Removed order with ID: {}", order.getId());
    }
}