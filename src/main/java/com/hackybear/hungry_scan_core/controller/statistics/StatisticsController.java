package com.hackybear.hungry_scan_core.controller.statistics;

import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserService userService;
    private final ExceptionHelper exceptionHelper;

    public StatisticsController(StatisticsService statisticsService,
                                UserService userService,
                                ExceptionHelper exceptionHelper) {
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.exceptionHelper = exceptionHelper;
    }

    @PostMapping("/dashboard/year")
    public ResponseEntity<?> getDashboardYearlyStats(@RequestBody Map<String, Object> params) {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(statisticsService.getYearlyStatistics(params, user));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/month")
    public ResponseEntity<?> getDashboardMonthlyStats(@RequestBody Map<String, Object> params) {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(statisticsService.getMonthlyStatistics(params, user));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/week")
    public ResponseEntity<?> getDashboardWeeklyStats(@RequestBody Map<String, Object> params) {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(statisticsService.getWeeklyStatistics(params, user));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/day")
    public ResponseEntity<?> getDashboardDailyStats(@RequestBody Map<String, Object> params) {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(statisticsService.getDailyStatistics(params, user));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}