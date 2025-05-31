package com.hackybear.hungry_scan_core.listener;

import com.hackybear.hungry_scan_core.entity.Order;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderListener {

    @PrePersist
    private void prePersist(final Order order) {
        LocalDateTime now = LocalDateTime.now();
        order.setOrderTime(now);
    }

}