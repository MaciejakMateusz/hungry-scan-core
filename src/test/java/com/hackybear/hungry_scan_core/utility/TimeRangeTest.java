package com.hackybear.hungry_scan_core.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeTest {

    @Test
    void intersect_NoOverlapAtBoundary_ReturnsNull() {
        TimeRange opening = new TimeRange(LocalTime.of(17, 0), LocalTime.of(2, 0));
        TimeRange additional = new TimeRange(LocalTime.of(15, 0), LocalTime.of(17, 0));

        TimeRange result = opening.intersect(additional);

        assertNull(result, "Expected null when there is no overlapping time range.");
    }

    @Test
    void intersect_OverlapAcrossMidnight_ReturnsCorrectRange() {
        TimeRange opening = new TimeRange(LocalTime.of(17, 0), LocalTime.of(2, 0));
        TimeRange night = new TimeRange(LocalTime.of(22, 0), LocalTime.of(3, 0));

        TimeRange result = opening.intersect(night);

        assertNotNull(result, "Expected non-null result for an overlapping range.");
        assertEquals(LocalTime.of(22, 0), result.getStartTime(), "Expected start time to be 22:00.");
        assertEquals(LocalTime.of(2, 0), result.getEndTime(), "Expected end time to be 02:00.");
    }

    @Test
    void intersect_NormalOverlap_ReturnsSubrange() {
        TimeRange morning = new TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0));
        TimeRange lunchSlot = new TimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0));

        TimeRange result = morning.intersect(lunchSlot);

        assertNotNull(result);
        assertEquals(LocalTime.of(10, 0), result.getStartTime());
        assertEquals(LocalTime.of(11, 0), result.getEndTime());
    }

    @Test
    void intersect_NoOverlapDuringDay_ReturnsNull() {
        TimeRange morning = new TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0));
        TimeRange earlyAfternoon = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));

        assertNull(morning.intersect(earlyAfternoon));
    }

    @Test
    void intersect_BothAcrossMidnight_PicksCorrectSlice() {
        TimeRange first = new TimeRange(LocalTime.of(23, 0), LocalTime.of(2, 0));
        TimeRange second = new TimeRange(LocalTime.of(1, 0), LocalTime.of(5, 0));

        TimeRange result = first.intersect(second);

        assertNotNull(result);
        assertEquals(LocalTime.of(1, 0), result.getStartTime());
        assertEquals(LocalTime.of(2, 0), result.getEndTime());
    }

    @Test
    void intersect_OtherIsNull_ReturnsNull() {
        TimeRange base = new TimeRange(LocalTime.of(8, 0), LocalTime.of(16, 0));
        assertNull(base.intersect(null));
    }
}