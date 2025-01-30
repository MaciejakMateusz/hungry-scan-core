package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "qr_scans_dates")
@Entity
public class ScanDate implements Serializable, Comparable<ScanDate> {

    public ScanDate(LocalDate date) {
        this.date = date;
    }

    public ScanDate() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "footprint", referencedColumnName = "footprint",
            insertable = false, updatable = false)
    private QrScan qrScan;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now();
    }

    @Override
    public int compareTo(ScanDate o) {
        return this.date.compareTo(o.date);
    }
}
