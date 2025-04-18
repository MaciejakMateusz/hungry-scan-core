package com.hackybear.hungry_scan_core.utility;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

@Getter
@Embeddable
@NoArgsConstructor
public class TimeRange implements Serializable {

    private LocalTime startTime;
    private LocalTime endTime;

    @Serial
    private static final long serialVersionUID = 1L;

    public TimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean includes(TimeRange other) {
        return !this.startTime.isAfter(other.getStartTime()) &&
                !this.endTime.isBefore(other.getEndTime());
    }

    @Override
    public String toString() {
        return "TimeRange {" +
                "startTime = " + startTime +
                "; endTime = " + endTime +
                '}';
    }
}
