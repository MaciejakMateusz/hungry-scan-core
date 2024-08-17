package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Table(name = "settings")
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalTime openingTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime closingTime;

    @NotNull
    @Column(nullable = false)
    private Long bookingDuration;

    @NotNull
    @Column(nullable = false)
    private Language language = Language.PL;

    @NotNull
    @Column(nullable = false)
    private Long employeeSessionTime;

    @NotNull
    @Column(nullable = false)
    private Long customerSessionTime;

    @NotNull
    @Column(nullable = false)
    private Short capacity;

    private boolean isOrderCommentAllowed;

    private boolean showUnavailablePositions;
}