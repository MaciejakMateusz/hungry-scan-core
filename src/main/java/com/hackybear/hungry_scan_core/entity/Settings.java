package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

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

    @Column(nullable = false, length = 2)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    @ElementCollection(targetClass = Language.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "settings_supported_languages",
            joinColumns = @JoinColumn(name = "settings_id")
    )
    @Column(name = "language", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    @NotEmpty
    @NotNull
    private Set<Language> supportedLanguages;

    private Long employeeSessionTime = 20L;

    private Long customerSessionTime = 20L;

    private Short capacity;

    private boolean orderCommentAllowed = true;

    private boolean waiterCommentAllowed = true;
}