package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.User;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getYearlyStatistics(Map<String, Object> params, User user);

    Map<String, Object> getMonthlyStatistics(Map<String, Object> params, User user);

    Map<String, Object> getWeeklyStatistics(Map<String, Object> params, User user);

    Map<String, Object> getDailyStatistics(Map<String, Object> params, User user);

}
