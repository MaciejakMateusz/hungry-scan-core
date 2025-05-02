package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "standard_plan")
@Entity
public class StandardDayPlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    @JsonIgnore
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "standardDayPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayTimeRange> timeRanges;
}
