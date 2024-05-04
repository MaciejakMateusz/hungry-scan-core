package pl.rarytas.hungry_scan_core.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
public class OrderSummaryListener {

    @PrePersist
    private void prePersist(final OrderSummary orderSummary) {
        LocalDateTime now = LocalDateTime.now();
        orderSummary.setInitialOrderTime(now);
    }

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