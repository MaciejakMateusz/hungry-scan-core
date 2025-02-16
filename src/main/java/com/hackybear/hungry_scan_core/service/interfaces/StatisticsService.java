package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.entity.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface StatisticsService {

    Map<String, Object> getYearlyScanStats(Map<String, Object> params, User user);

    Map<String, Object> getMonthlyScanStats(Map<String, Object> params, User user);

    Map<String, Object> getWeeklyScanStats(Map<String, Object> params, User user);

    Map<String, Object> getDailyScanStats(Map<String, Object> params, User user);

    Set<MenuItemViewCountDTO> getYearlyMenuItemViewsStats(Long menuId, int year);

    Set<MenuItemViewCountDTO> getMonthlyMenuItemViewsStats(Long menuId, int year, int month);

    Set<MenuItemViewCountDTO> getWeeklyMenuItemViewsStats(Long menuId, int year, int week);

    Set<MenuItemViewCountDTO> getDailyMenuItemViewsStats(Long menuId, LocalDate date);
}
