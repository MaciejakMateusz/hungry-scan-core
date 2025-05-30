package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

@Getter
@Setter
@Table(name = "settings")
@Entity
public class Settings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @NotNull
    @Column(nullable = false)
    private LocalTime openingTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime closingTime;

    private Long bookingDuration;

    private Language language = Language.PL;

    private Long employeeSessionTime = 20L;

    private Long customerSessionTime = 20L;

    private Short capacity;

    private boolean orderCommentAllowed = true;

    private boolean waiterCommentAllowed = true;
}