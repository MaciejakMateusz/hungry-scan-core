package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.QrScanEvent;
import com.hackybear.hungry_scan_core.interfaces.aggregators.ScanAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QrScanEventRepository extends JpaRepository<QrScanEvent, Long> {

    List<QrScanEvent> findByVisitorId(String visitorId);

    @Query(value = "SELECT MONTH(scanned_at) AS period, " +
            "COUNT(*) AS total, " +
            "COUNT(DISTINCT visitor_id) AS uniqueCount " +
            "FROM qr_scan_events " +
            "WHERE restaurant_id = :restaurantId " +
            "AND YEAR(scanned_at) = :year " +
            "GROUP BY MONTH(scanned_at)",
            nativeQuery = true)
    List<ScanAggregation> aggregateByMonth(@Param("restaurantId") Long restaurantId,
                                           @Param("year") int year);

    @Query(value = "SELECT DAY(scanned_at) AS period, " +
            "COUNT(*) AS total, " +
            "COUNT(DISTINCT visitor_id) AS uniqueCount " +
            "FROM qr_scan_events " +
            "WHERE restaurant_id = :restaurantId AND YEAR(scanned_at) = :year " +
            "AND MONTH(scanned_at) = :month " +
            "GROUP BY DAY(scanned_at)",
            nativeQuery = true)
    List<ScanAggregation> aggregateByDay(@Param("restaurantId") Long restaurantId,
                                         @Param("year") int year,
                                         @Param("month") int month);

    @Query(value = """
            SELECT (WEEKDAY(scanned_at) + 1) AS period,
                   COUNT(*) AS total,
                   COUNT(DISTINCT visitor_id) AS uniqueCount
            FROM qr_scan_events
            WHERE restaurant_id = :restaurantId
              AND YEARWEEK(scanned_at, 3) = :yearWeek
            GROUP BY (WEEKDAY(scanned_at) + 1)
            """, nativeQuery = true)
    List<ScanAggregation> aggregateByIsoWeek(@Param("restaurantId") Long restaurantId,
                                             @Param("yearWeek") int yearWeek);

    @Query(value = "SELECT HOUR(scanned_at) AS period, " +
            "COUNT(*) AS total, " +
            "COUNT(DISTINCT visitor_id) AS uniqueCount " +
            "FROM qr_scan_events " +
            "WHERE restaurant_id = :restaurantId " +
            "AND CAST(scanned_at AS DATE) = :date " +
            "GROUP BY HOUR(scanned_at)",
            nativeQuery = true)
    List<ScanAggregation> aggregateByHour(@Param("restaurantId") Long restaurantId,
                                          @Param("date") LocalDate date);
}
