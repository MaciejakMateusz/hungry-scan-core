package com.hackybear.hungry_scan_core.utility;

import java.time.LocalTime;

/**
 * A wall-clock point expressed as “minutes past midnight”.
 * <p>
 * • When the point is an <strong>end boundary</strong> and the LocalTime is 00:00,
 * it is normalised to 24 : 00 (1440) so that a range like 12 : 00→00 : 00 is
 * treated as <em>same-day up to midnight</em>, not “into tomorrow”.
 */
public record ClockPoint(int minutes) implements Comparable<ClockPoint> {

    public static ClockPoint start(LocalTime t) {
        return new ClockPoint(t.getHour() * 60 + t.getMinute());
    }

    public static ClockPoint end(LocalTime t) {
        int m = t.getHour() * 60 + t.getMinute();
        return new ClockPoint(m == 0 ? 24 * 60 : m);
    }

    public int asMinutes() {
        return minutes;
    }

    public boolean isBefore(ClockPoint other) {
        return this.minutes < other.minutes;
    }

    public int distanceTo(ClockPoint other) {
        return other.minutes - this.minutes;
    }

    @Override
    public int compareTo(ClockPoint o) {
        return Integer.compare(minutes, o.minutes);
    }
}