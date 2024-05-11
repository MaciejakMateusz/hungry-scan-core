package com.hackybear.hungry_scan_core.listener;

import com.hackybear.hungry_scan_core.entity.OrderSummary;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class OrderSummaryListener {

    @PostPersist
    public void postPersist(final OrderSummary orderSummary) {
        log.info("New order received with ID: {}, time of initial order: {}",
                orderSummary.getId(),
                orderSummary.getInitialOrderTime());
    }

    @PostUpdate
    public void postUpdate(final OrderSummary orderSummary) {
        log.info("Updated order summary with ID: {}, time of initial order: {}",
                orderSummary.getId(),
                orderSummary.getInitialOrderTime());
    }

    @PostRemove
    public void postRemove(final OrderSummary orderSummary) {
        log.info("Removed order summary with ID: {}", orderSummary.getId());
    }
}