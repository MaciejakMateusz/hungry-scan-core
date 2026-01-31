package com.hackybear.hungry_scan_core;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

public final class MySqlDateFunctions {

    private MySqlDateFunctions() {
    }

    /**
     * MySQL WEEKDAY(date): 0=Mon ... 6=Sun
     */
    public static Integer weekday(Timestamp ts) {
        if (ts == null) return null;
        LocalDateTime ldt = ts.toLocalDateTime();
        int isoDow = ldt.getDayOfWeek().getValue(); // 1=Mon..7=Sun
        return isoDow - 1;
    }

    /**
     * MySQL YEARWEEK(date, 3): ISO week-based-year + ISO week number => YYYYWW
     * mode=3 means Monday-first and week1 has >=4 days (ISO-8601 behavior).
     */
    public static Integer yearWeek(Timestamp ts, int mode) {
        if (ts == null) return null;
        if (mode != 3) {
            throw new IllegalArgumentException("Only mode=3 is supported here");
        }
        LocalDateTime ldt = ts.toLocalDateTime();
        WeekFields wf = WeekFields.ISO;
        int week = ldt.get(wf.weekOfWeekBasedYear());
        int weekYear = ldt.get(wf.weekBasedYear());
        return weekYear * 100 + week;
    }
}
