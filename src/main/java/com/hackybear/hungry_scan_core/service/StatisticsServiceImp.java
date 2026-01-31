package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.service.helpers.MenuItemViewEventAggregator;
import com.hackybear.hungry_scan_core.service.helpers.ScanEventAggregator;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImp implements StatisticsService {

    private final ScanEventAggregator scanEventAggregator;
    private final MenuItemViewEventAggregator viewEventAggregator;

    @Override
    @Cacheable(value = STATS_SCANS_YEARLY, key = "#params.get('year') + '-' + #restaurantId")
    public Map<String, Object> getYearlyScanStats(Map<String, Object> params, Long restaurantId) {
        Integer year = (Integer) params.get("year");
        return scanEventAggregator.projectYearlyScans(restaurantId, year);
    }

    @Override
    @Cacheable(value = STATS_SCANS_MONTHLY, key = "#params.get('year') + '-' + #params.get('month') + '-' + #restaurantId")
    public Map<String, Object> getMonthlyScanStats(Map<String, Object> params, Long restaurantId) {
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("month");
        return scanEventAggregator.projectMonthlyScans(restaurantId, year, month);
    }

    @Override
    @Cacheable(value = STATS_SCANS_WEEKLY, key = "#params.get('year') + '-' + #params.get('week') + '-' + #restaurantId")
    public Map<String, Object> getWeeklyScanStats(Map<String, Object> params, Long restaurantId) {
        Integer year = (Integer) params.get("year");
        Integer week = (Integer) params.get("week");
        return scanEventAggregator.projectWeeklyScans(restaurantId, year, week);
    }

    @Override
    @Cacheable(value = STATS_SCANS_DAILY, key = "#day.toString() + '-' + #restaurantId")
    public Map<String, Object> getDailyScanStats(LocalDate day, Long restaurantId) {
        return scanEventAggregator.projectDailyScans(restaurantId, day);
    }

    @Override
    @Cacheable(value = STATS_MENU_ITEMS_YEARLY, key = "#menuId + '-' + #year")
    public Set<MenuItemViewCountDTO> getYearlyMenuItemViewsStats(Long menuId, int year) {
        return viewEventAggregator.projectYearlyMenuItemViews(menuId, year);
    }

    @Override
    @Cacheable(value = STATS_MENU_ITEMS_MONTHLY, key = "#menuId + '-' + #year + '-' + #month")
    public Set<MenuItemViewCountDTO> getMonthlyMenuItemViewsStats(Long menuId, int year, int month) {
        return viewEventAggregator.projectMonthlyMenuItemViews(menuId, year, month);
    }

    @Override
    @Cacheable(value = STATS_MENU_ITEMS_WEEKLY, key = "#menuId + '-' + #year + '-w' + #week")
    public Set<MenuItemViewCountDTO> getWeeklyMenuItemViewsStats(Long menuId, int year, int week) {
        return viewEventAggregator.projectWeeklyMenuItemViews(menuId, year, week);
    }

    @Override
    @Cacheable(value = STATS_MENU_ITEMS_DAILY, key = "#menuId + '-' + #date.toString()")
    public Set<MenuItemViewCountDTO> getDailyMenuItemViewsStats(Long menuId, LocalDate date) {
        return viewEventAggregator.projectDailyMenuItemViews(menuId, date);
    }

}