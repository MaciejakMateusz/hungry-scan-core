package com.hackybear.hungry_scan_core.service.helpers;

import com.hackybear.hungry_scan_core.interfaces.aggregators.ScanAggregation;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.utility.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanEventAggregator {

    private final QrScanEventRepository scanEventRepository;

    public Map<String, Object> projectYearlyScans(Long restaurantId, Integer year) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByMonth(restaurantId, year);
        List<ScanAggregation> prevPeriodAggregation = scanEventRepository.aggregateByMonth(restaurantId, year - 1);
        Result result = getResult(aggregation);

        for (int m = 1; m <= 12; m++) {
            AggregationResult data = getAggregationResult(result, m);
            prepareChartMaps(result, data, m);
        }
        preparePieChartData(prevPeriodAggregation, aggregation, result);
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectMonthlyScans(Long restaurantId, Integer year, Integer month) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByDay(restaurantId, year, month);
        List<ScanAggregation> prevPeriodAggregation = scanEventRepository.aggregateByDay(restaurantId, year, month - 1);
        Result result = getResult(aggregation);

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            AggregationResult data = getAggregationResult(result, d);
            prepareChartMaps(result, data, d);
        }
        preparePieChartData(prevPeriodAggregation, aggregation, result);
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectWeeklyScans(Long restaurantId, Integer year, Integer week) {
        WeekFields wf = WeekFields.ISO;

        LocalDate weekStart = LocalDate.now()
                .with(wf.weekBasedYear(), year)
                .with(wf.weekOfWeekBasedYear(), week)
                .with(wf.dayOfWeek(), 1);

        LocalDate prevWeekStart = weekStart.minusWeeks(1);

        int yearWeek = weekStart.get(wf.weekBasedYear()) * 100 + weekStart.get(wf.weekOfWeekBasedYear());
        int prevYearWeek = prevWeekStart.get(wf.weekBasedYear()) * 100 + prevWeekStart.get(wf.weekOfWeekBasedYear());

        List<ScanAggregation> aggregation = scanEventRepository.aggregateByIsoWeek(restaurantId, yearWeek);
        List<ScanAggregation> prevPeriodAggregation = scanEventRepository.aggregateByIsoWeek(restaurantId, prevYearWeek);
        Result result = getResult(aggregation);

        for (int w = 1; w <= 7; w++) {
            AggregationResult data = getAggregationResult(result, w);
            prepareChartMaps(result, data, w);
        }
        preparePieChartData(prevPeriodAggregation, aggregation, result);
        return getDataProjection(aggregation, result);
    }

    public Map<String, Object> projectDailyScans(Long restaurantId, java.time.LocalDate date) {
        List<ScanAggregation> aggregation = scanEventRepository.aggregateByHour(restaurantId, date);
        List<ScanAggregation> prevPeriodAggregation = scanEventRepository.aggregateByHour(restaurantId, date.minusDays(1L));
        Result result = getResult(aggregation);

        for (int h = 0; h < 24; h++) {
            AggregationResult data = getAggregationResult(result, h);
            prepareChartMaps(result, data, h);
        }
        preparePieChartData(prevPeriodAggregation, aggregation, result);
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

    private static Map<String, Object> getDataProjection(List<ScanAggregation> aggregation, Result result) {
        Map<String, Object> projection = constructInitialProjection(result);
        int totalScans = aggregation.stream().mapToInt(ScanAggregation::getTotal).sum();
        int totalUnique = aggregation.stream().mapToInt(ScanAggregation::getUniqueCount).sum();
        int totalRepeated = totalScans - totalUnique;
        projection.put("total", totalScans);
        projection.put("totalUnique", totalUnique);
        projection.put("totalRepeated", totalRepeated);
        return projection;
    }

    private static Result getResult(List<ScanAggregation> aggregation) {
        Map<Integer, ScanAggregation> aggregationByHour = aggregation.stream()
                .collect(Collectors.toMap(ScanAggregation::getPeriod, Function.identity()));

        List<Map<String, Object>> uniqueLineData = new ArrayList<>();
        List<Map<String, Object>> repeatedLineData = new ArrayList<>();
        List<Map<String, Object>> barChartData = new ArrayList<>();
        Map<String, Object> pieChartData = new HashMap<>();
        return new Result(aggregationByHour, uniqueLineData, repeatedLineData, barChartData, pieChartData);
    }

    private static Map<String, Object> constructInitialProjection(Result result) {
        Map<String, Object> projection = new HashMap<>();
        projection.put("lineChart", List.of(
                Map.of(
                        "id", "uniqueScans",
                        "data", result.uniqueLineData),
                Map.of(
                        "id", "repeatedScans",
                        "data", result.repeatedLineData)
        ));
        projection.put("barChart", result.barChartData);
        projection.put("pieChart", result.pieChartData);
        return projection;
    }

    private void preparePieChartData(List<ScanAggregation> prevPeriodAggregation,
                                     List<ScanAggregation> aggregation,
                                     Result result) {
        Integer prevUniqueCount = getCount(prevPeriodAggregation, ScanAggregation::getUniqueCount);
        Integer prevTotalCount = getCount(prevPeriodAggregation, ScanAggregation::getTotal);
        Integer uniqueCount = getCount(aggregation, ScanAggregation::getUniqueCount);
        Integer totalCount = getCount(aggregation, ScanAggregation::getTotal);
        Integer repeatedCount = totalCount - uniqueCount;
        Integer prevRepeatedCount = prevTotalCount - prevUniqueCount;

        result.pieChartData.putAll(Map.of(
                "unique", uniqueCount,
                "repeated", repeatedCount,
                "uniquePrevPeriodDifference", getDifferencePercentage(uniqueCount, prevUniqueCount),
                "repeatedPrevPeriodDifference", getDifferencePercentage(repeatedCount, prevRepeatedCount)));
    }

    private Integer getCount(List<ScanAggregation> aggregation, Function<ScanAggregation, Integer> function) {
        return aggregation.stream()
                .map(function)
                .reduce(Integer::sum)
                .orElse(0);
    }

    private BigDecimal getDifferencePercentage(Integer current, Integer previous) {
        if (Objects.isNull(previous) || previous.equals(0)) {
            return BigDecimal.ZERO;
        }

        double currentDouble = current.doubleValue();
        double previousDouble = previous.doubleValue();

        double difference = ((currentDouble - previousDouble) / previousDouble) * 100;
        return Money.of(difference);
    }

    private record Result(Map<Integer, ScanAggregation> aggregation,
                          List<Map<String, Object>> uniqueLineData,
                          List<Map<String, Object>> repeatedLineData,
                          List<Map<String, Object>> barChartData,
                          Map<String, Object> pieChartData) {
    }

    private record AggregationResult(int unique, int repeated) {
    }

}
