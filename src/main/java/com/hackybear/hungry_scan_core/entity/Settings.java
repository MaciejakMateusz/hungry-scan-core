package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "settings_operating_hours",
            joinColumns = @JoinColumn(name = "settings_id")
    )
    @MapKeyColumn(name = "day_of_week")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<DayOfWeek, TimeRange> operatingHours = new EnumMap<>(DayOfWeek.class);

    private Long bookingDuration;

    private Language language = Language.PL;

    private Long employeeSessionTime = 20L;

    private Long customerSessionTime = 20L;

    private Short capacity;

    private boolean orderCommentAllowed = true;

    private boolean waiterCommentAllowed = true;
}