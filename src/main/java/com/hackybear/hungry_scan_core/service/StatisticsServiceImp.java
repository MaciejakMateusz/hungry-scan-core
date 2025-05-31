package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.service.helpers.MenuItemViewEventAggregator;
import com.hackybear.hungry_scan_core.service.helpers.ScanEventAggregator;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImp implements StatisticsService {

    private final ScanEventAggregator scanEventAggregator;
    private final MenuItemViewEventAggregator viewEventAggregator;

    @Override
    public Map<String, Object> getYearlyScanStats(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        return scanEventAggregator.projectYearlyScans(restaurantId, year);
    }

    @Override
    public Map<String, Object> getMonthlyScanStats(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("month");
        return scanEventAggregator.projectMonthlyScans(restaurantId, year, month);
    }

    @Override
    public Map<String, Object> getWeeklyScanStats(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        Integer week = (Integer) params.get("week");
        return scanEventAggregator.projectWeeklyScans(restaurantId, year, week);
    }

    @Override
    public Map<String, Object> getDailyScanStats(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Instant instant = Instant.parse((String) params.get("day"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return scanEventAggregator.projectDailyScans(restaurantId, localDateTime.toLocalDate());
    }

    @Override
    public Set<MenuItemViewCountDTO> getYearlyMenuItemViewsStats(Long menuId, int year) {
        return viewEventAggregator.projectYearlyMenuItemViews(menuId, year);
    }

    @Override
    public Set<MenuItemViewCountDTO> getMonthlyMenuItemViewsStats(Long menuId, int year, int month) {
        return viewEventAggregator.projectMonthlyMenuItemViews(menuId, year, month);
    }

    @Override
    public Set<MenuItemViewCountDTO> getWeeklyMenuItemViewsStats(Long menuId, int year, int week) {
        return viewEventAggregator.projectWeeklyMenuItemViews(menuId, year, week);
    }

    @Override
    public Set<MenuItemViewCountDTO> getDailyMenuItemViewsStats(Long menuId, LocalDate date) {
        return viewEventAggregator.projectDailyMenuItemViews(menuId, date);
    }

}