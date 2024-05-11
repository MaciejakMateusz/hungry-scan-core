package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "waiter_calls")
@Entity
public class WaiterCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(nullable = false)
    private LocalDateTime callTime;

    private LocalDateTime resolvedTime;

    private boolean isResolved = false;

    @PrePersist
    private void prePersist() {
        this.callTime = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.resolvedTime = LocalDateTime.now();
    }
}