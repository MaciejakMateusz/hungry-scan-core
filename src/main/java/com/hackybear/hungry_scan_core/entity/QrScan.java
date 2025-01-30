package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "qr_scans")
@Entity
public class QrScan implements Serializable {

    @Id
    @Column(nullable = false, unique = true)
    @NotBlank
    private String footprint;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotBlank
    private String restaurantToken;

    @OneToMany(mappedBy = "qrScan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanDate> scanDates;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer quantity;
}
