package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "waiter_calls")
@Entity
public class WaiterCall implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

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