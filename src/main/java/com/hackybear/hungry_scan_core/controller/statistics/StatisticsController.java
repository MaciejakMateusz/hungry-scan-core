package com.hackybear.hungry_scan_core.controller.statistics;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserService userService;
    private final ExceptionHelper exceptionHelper;

    @PostMapping("/dashboard/year/scans")
    public ResponseEntity<?> getYearlyScanStats(@RequestBody Map<String, Object> params) {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(statisticsService.getYearlyScanStats(params, restaurantId));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/year/menu-item-views")
    public ResponseEntity<?> getYearlyMenuItemViewsStats(@RequestBody Map<String, Object> params) {
        Long menuId = Long.valueOf(params.get("menuId").toString());
        int year = (int) params.get("year");
        return ResponseEntity.ok(statisticsService.getYearlyMenuItemViewsStats(menuId, year));
    }

    @PostMapping("/dashboard/month/scans")
    public ResponseEntity<?> getMonthlyScanStats(@RequestBody Map<String, Object> params) {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(statisticsService.getMonthlyScanStats(params, restaurantId));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/month/menu-item-views")
    public ResponseEntity<?> getMonthlyMenuItemViewsStats(@RequestBody Map<String, Object> params) {
        Long menuId = Long.valueOf(params.get("menuId").toString());
        int year = (int) params.get("year");
        int month = (int) params.get("month");
        return ResponseEntity.ok(statisticsService.getMonthlyMenuItemViewsStats(menuId, year, month));
    }

    @PostMapping("/dashboard/week/scans")
    public ResponseEntity<?> getWeeklyScanStats(@RequestBody Map<String, Object> params) {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(statisticsService.getWeeklyScanStats(params, restaurantId));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/week/menu-item-views")
    public ResponseEntity<?> getWeeklyMenuItemViewsStats(@RequestBody Map<String, Object> params) {
        Long menuId = Long.valueOf(params.get("menuId").toString());
        int year = (int) params.get("year");
        int week = (int) params.get("week");
        return ResponseEntity.ok(statisticsService.getWeeklyMenuItemViewsStats(menuId, year, week));
    }

    @PostMapping("/dashboard/day/scans")
    public ResponseEntity<?> getDailyScanStats(@RequestBody Map<String, Object> params) {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(statisticsService.getDailyScanStats(params, restaurantId));
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    @PostMapping("/dashboard/day/menu-item-views")
    public ResponseEntity<?> getDailyMenuItemViewsStats(@RequestBody Map<String, Object> params) {
        Long menuId = Long.valueOf(params.get("menuId").toString());
        Instant instant = Instant.parse((String) params.get("day"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return ResponseEntity.ok(statisticsService.getDailyMenuItemViewsStats(menuId, localDateTime.toLocalDate()));
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}