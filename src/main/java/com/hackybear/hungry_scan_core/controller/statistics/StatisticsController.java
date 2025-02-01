package com.hackybear.hungry_scan_core.controller.statistics;

import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping("/dashboard/year")
    public ResponseEntity<?> getDashboardYearlyStats(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(statisticsService.getYearlyStatistics(params));
    }

    @PostMapping("/dashboard/month")
    public ResponseEntity<?> getDashboardMonthlyStats(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(statisticsService.getMonthlyStatistics(params));
    }

    @PostMapping("/dashboard/week")
    public ResponseEntity<?> getDashboardWeeklyStats(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(statisticsService.getWeeklyStatistics(params));
    }

    @PostMapping("/dashboard/day")
    public ResponseEntity<?> getDashboardDailyStats(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(statisticsService.getDailyStatistics(params));
    }
}