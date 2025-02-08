package com.hackybear.hungry_scan_core.service.helpers;

import com.hackybear.hungry_scan_core.interfaces.aggregators.ScanAggregation;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ScanEventAggregator {

    private final QrScanEventRepository ser;

    public ScanEventAggregator(QrScanEventRepository ser) {
        this.ser = ser;
    }

    public Map<String, Object> projectYearlyScans(Long restaurantId, Integer year) {
        List<ScanAggregation> aggregation = ser.aggregateByMonth(restaurantId, year);
        Result result = getResult(aggregation);

        for (int m = 1; m <= 12; m++) {
            AggregationResult data = getAggregationResult(result, m);
            prepareChartMaps(result, data, m);
        }
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectMonthlyScans(Long restaurantId, Integer year, Integer month) {
        List<ScanAggregation> aggregation = ser.aggregateByDay(restaurantId, year, month);
        Result result = getResult(aggregation);

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            AggregationResult data = getAggregationResult(result, d);
            prepareChartMaps(result, data, d);
        }
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectWeeklyScans(Long restaurantId, Integer year, Integer week) {
        List<ScanAggregation> aggregation = ser.aggregateByDayOfWeek(restaurantId, year, week);
        Result result = getResult(aggregation);

        for (int w = 1; w <= 7; w++) {
            AggregationResult data = getAggregationResult(result, w);
            prepareChartMaps(result, data, w);
        }
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectDailyScans(Long restaurantId, java.time.LocalDate date) {
        List<ScanAggregation> aggregation = ser.aggregateByHour(restaurantId, date);
        Result result = getResult(aggregation);

        for (int h = 0; h < 24; h++) {
            AggregationResult data = getAggregationResult(result, h);
            prepareChartMaps(result, data, h);
        }
        return getDataProjection(aggregation, result);
    }

    private static AggregationResult getAggregationResult(Result result, int d) {
        ScanAggregation data = result.aggregation.get(d);
        int total = data != null ? data.getTotal() : 0;
        int unique = data != null ? data.getUniqueCount() : 0;
        int repeated = total - unique;
        return new AggregationResult(unique, repeated);
    }

    private void prepareChartMaps(Result result, AggregationResult data, Integer index) {
        result.uniqueLineData.add(Map.of(
                "x", index,
                "y", data.unique));
        result.repeatedLineData.add(Map.of(
                "x", index,
                "y", data.repeated));
        result.barChartData.add(Map.of(
                "x", index,
                "uniqueScans", data.unique,
                "repeatedScans", data.repeated));
    }

    private Map<String, Object> getDataProjection(List<ScanAggregation> aggregation, Result result) {
        int totalScans = aggregation.stream().mapToInt(ScanAggregation::getTotal).sum();
        int totalUnique = aggregation.stream().mapToInt(ScanAggregation::getUniqueCount).sum();
        int totalRepeated = totalScans - totalUnique;

        return Map.of(
                "lineChart", List.of(
                        Map.of(
                                "id", "uniqueScans",
                                "data", result.uniqueLineData),
                        Map.of(
                                "id", "repeatedScans",
                                "data", result.repeatedLineData)
                ),
                "barChart", result.barChartData,
                "total", totalScans,
                "totalUnique", totalUnique,
                "totalRepeated", totalRepeated
        );
    }

    private Result getResult(List<ScanAggregation> aggregation) {
        Map<Integer, ScanAggregation> aggregationByHour = aggregation.stream()
                .collect(Collectors.toMap(ScanAggregation::getPeriod, Function.identity()));

        List<Map<String, Object>> uniqueLineData = new ArrayList<>();
        List<Map<String, Object>> repeatedLineData = new ArrayList<>();
        List<Map<String, Object>> barChartData = new ArrayList<>();
        return new Result(aggregationByHour, uniqueLineData, repeatedLineData, barChartData);
    }

    private record Result(Map<Integer, ScanAggregation> aggregation,
                          List<Map<String, Object>> uniqueLineData,
                          List<Map<String, Object>> repeatedLineData,
                          List<Map<String, Object>> barChartData) {
    }

    private record AggregationResult(int unique, int repeated) {
    }

}
