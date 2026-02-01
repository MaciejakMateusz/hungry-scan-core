package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "qr_scan_events")
@Entity
public class QrScanEvent implements Serializable, Comparable<QrScanEvent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String visitorId;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private Long restaurantId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime scannedAt;

    @PrePersist
    public void prePersist() {
        this.scannedAt = LocalDateTime.now();
    }

    @Override
    public int compareTo(QrScanEvent o) {
        return this.scannedAt.compareTo(o.scannedAt);
    }
}
