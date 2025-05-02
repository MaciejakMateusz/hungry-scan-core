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
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TimeRange {" +
                "startTime = " + startTime +
                "; endTime = " + endTime +
                '}';
    }
}
