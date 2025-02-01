package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.interfaces.ScanAggregation;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImp implements StatisticsService {

    private final QrScanEventRepository scanEventRepository;

    public StatisticsServiceImp(QrScanEventRepository scanEventRepository) {
        this.scanEventRepository = scanEventRepository;
    }

    @Override
    public Map<String, Object> getYearlyStatistics(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        return projectYearlyScans(restaurantId, year);
    }

    @Override
    public Map<String, Object> getMonthlyStatistics(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("month");
        return projectMonthlyScans(restaurantId, year, month);
    }

    @Override
    public Map<String, Object> getWeeklyStatistics(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        Integer week = (Integer) params.get("week");
        return projectWeeklyScans(restaurantId, year, week);
    }

    @Override
    public Map<String, Object> getDailyStatistics(Map<String, Object> params, User user) {
        Long restaurantId = user.getActiveRestaurantId();
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("week");
        Integer day = (Integer) params.get("day");
        LocalDate localDate = LocalDate.of(year, month, day);
        return projectDailyScans(restaurantId, localDate);
    }

    private Map<String, Object> projectYearlyScans(Long restaurantId, Integer year) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByMonth(restaurantId, year);
        Result result = getResult(aggregation);

        for (int m = 1; m <= 12; m++) {
            AggregationResult data = getAggregationResult(result, m);
            prepareChartMaps(result, data, m, "month");
        }
        return getDataProjection(aggregation, result);
    }

    private Map<String, Object> projectMonthlyScans(Long restaurantId, Integer year, Integer month) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByDay(restaurantId, year, month);
        Result result = getResult(aggregation);

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            AggregationResult data = getAggregationResult(result, d);
            prepareChartMaps(result, data, d, "day");
        }
        return getDataProjection(aggregation, result);
    }

    private Map<String, Object> projectWeeklyScans(Long restaurantId, Integer year, Integer week) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByDayOfWeek(restaurantId, year, week);
        Result result = getResult(aggregation);

        for (int w = 1; w <= 7; w++) {
            AggregationResult data = getAggregationResult(result, w);
            prepareChartMaps(result, data, w, "day");
        }
        return getDataProjection(aggregation, result);
    }

    private Map<String, Object> projectDailyScans(Long restaurantId, java.time.LocalDate date) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByHour(restaurantId, date);
        Result result = getResult(aggregation);

        for (int h = 0; h < 24; h++) {
            AggregationResult data = getAggregationResult(result, h);
            prepareChartMaps(result, data, h, "hour");
        }
        return getDataProjection(aggregation, result);
    }

    private Result getResult(List<ScanAggregation> aggregation) {
        Map<Integer, ScanAggregation> aggregationByHour = aggregation.stream()
                .collect(Collectors.toMap(ScanAggregation::getPeriod, Function.identity()));

        List<Map<String, Object>> uniqueLineData = new ArrayList<>();
        List<Map<String, Object>> repeatedLineData = new ArrayList<>();
        List<Map<String, Object>> barChartData = new ArrayList<>();
        return new Result(aggregationByHour, uniqueLineData, repeatedLineData, barChartData);
    }

    private static AggregationResult getAggregationResult(Result result, int d) {
        ScanAggregation data = result.aggregation.get(d);
        int total = data != null ? data.getTotal() : 0;
        int unique = data != null ? data.getUniqueCount() : 0;
        int repeated = total - unique;
        return new AggregationResult(unique, repeated);
    }

    private void prepareChartMaps(Result result, AggregationResult data, Integer index, String type) {
        result.uniqueLineData.add(Map.of("x", index, "y", data.unique));
        result.repeatedLineData.add(Map.of("x", index, "y", data.repeated));
        result.barChartData.add(Map.of(type, index, "uniqueScans", data.unique, "repeatedScans", data.repeated));
    }

    private Map<String, Object> getDataProjection(List<ScanAggregation> aggregation, Result result) {
        int totalScans = aggregation.stream().mapToInt(ScanAggregation::getTotal).sum();
        int totalUnique = aggregation.stream().mapToInt(ScanAggregation::getUniqueCount).sum();
        int totalRepeated = totalScans - totalUnique;

        return Map.of(
                "lineChart", List.of(
                        Map.of("id", "uniqueScans", "data", result.uniqueLineData),
                        Map.of("id", "repeatedScans", "data", result.repeatedLineData)
                ),
                "barChart", result.barChartData,
                "total", totalScans,
                "totalUnique", totalUnique,
                "totalRepeated", totalRepeated
        );
    }

    private record Result(Map<Integer, ScanAggregation> aggregation,
                          List<Map<String, Object>> uniqueLineData,
                          List<Map<String, Object>> repeatedLineData,
                          List<Map<String, Object>> barChartData) {
    }

    private record AggregationResult(int unique, int repeated) {
    }

}