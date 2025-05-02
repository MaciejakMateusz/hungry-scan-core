package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;


@Getter
@Setter
@EqualsAndHashCode
@Table(name = "time_range")
@Entity
public class DayTimeRange implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "standard_day_plan_id", nullable = false)
    @JsonIgnore
    private StandardDayPlan standardDayPlan;

    private LocalTime startTime;

    private LocalTime endTime;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "TimeRange {" +
                "startTime = " + startTime +
                "; endTime = " + endTime +
                '}';
    }
}