package com.hackybear.hungry_scan_core.utility;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
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

    public TimeRange intersect(TimeRange other) {
        if (other == null) {
            return null;
        }
        final int minutesPerDay = 1440;

        var seg1 = toNormalizedSegments(this);
        var seg2 = toNormalizedSegments(other);

        List<int[]> intersections = new ArrayList<>();
        for (int[] a : seg1) {
            for (int[] b : seg2) {
                int start = Math.max(a[0], b[0]);
                int end = Math.min(a[1], b[1]);
                if (end > start) {
                    intersections.add(new int[]{start, end});
                }
            }
        }
        if (intersections.isEmpty()) {
            return null;
        }

        int[] best = intersections.stream()
                .max(Comparator.comparingInt(seg -> seg[1] - seg[0]))
                .get();

        int rawStart = best[0] % minutesPerDay;
        int rawEnd = best[1] % minutesPerDay;
        LocalTime newStart = LocalTime.of(rawStart / 60, rawStart % 60);
        LocalTime newEnd = LocalTime.of(rawEnd / 60, rawEnd % 60);
        return new TimeRange(newStart, newEnd);
    }

    private static List<int[]> toNormalizedSegments(TimeRange tr) {
        final int minutesPerDay = 1440;
        int s = tr.startTime.getHour() * 60 + tr.startTime.getMinute();
        int e = tr.endTime.getHour() * 60 + tr.endTime.getMinute();
        if (e > s) {
            return List.of(
                    new int[]{s, e},
                    new int[]{s + minutesPerDay, e + minutesPerDay}
            );
        } else {
            return List.of(
                    new int[]{s, e + minutesPerDay}
            );
        }
    }

    @Override
    public String toString() {
        return "TimeRange {" +
                "startTime = " + startTime +
                "; endTime = " + endTime +
                '}';
    }
}
