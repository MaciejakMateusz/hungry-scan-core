package com.hackybear.hungry_scan_core.controller.statistics;

import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatisticsControllerTest {

    private static final String RESTAURANT_TOKEN = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";
    private static final int YEAR_2024 = 2024;

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Order(1)
    @Sql({"/data-h2.sql", "/test-packs/qr-scans.sql"})
    @Test
    void init() {
        log.info("Initializing H2 database with QR scans data...");
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetDashboardYearlyStats() throws Exception {
        Map<String, Object> params = Map.of(
                "restaurantToken", RESTAURANT_TOKEN,
                "year", YEAR_2024
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/year",
                params,
                status().isOk()
        );

        assertYearlyResponse(response);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetDashboardMonthlyStats() throws Exception {
        Map<String, Object> params = Map.of(
                "restaurantToken", RESTAURANT_TOKEN,
                "year", YEAR_2024,
                "month", 1
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/month",
                params,
                status().isOk()
        );

        assertMonthlyResponse(response);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetDashboardWeeklyStats() throws Exception {
        Map<String, Object> params = Map.of(
                "restaurantToken", RESTAURANT_TOKEN,
                "year", YEAR_2024,
                "week", 1
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/week",
                params,
                status().isOk()
        );

        assertWeeklyResponse(response);
    }

    @Test
    @Transactional
    @Rollback
    void shouldGetDashboardDailyStats() throws Exception {
        Map<String, Object> params = Map.of(
                "restaurantToken", RESTAURANT_TOKEN,
                "year", YEAR_2024,
                "week", 1,
                "day", 15
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/day",
                params,
                status().isOk()
        );

        assertDailyResponse(response);
    }

    // ---------------------------------------------------------------------------------------
    // Assertion Methods
    // ---------------------------------------------------------------------------------------

    private void assertYearlyResponse(Map<?, ?> response) {
        assertEquals(9, response.get("totalUnique"));
        assertEquals(5, response.get("totalRepeated"));
        assertEquals(14, response.get("total"));

        assertYearlyLineChart(response);
        assertYearlyBarChart(response);
    }

    private void assertMonthlyResponse(Map<?, ?> response) {
        assertEquals(7, response.get("totalUnique"));
        assertEquals(1, response.get("totalRepeated"));
        assertEquals(8, response.get("total"));

        List<Map<?, ?>> barChartData = castList(response.get("barChart"));
        List<Map<?, ?>> lineChartData = castList(response.get("lineChart"));

        // Basic size checks
        assertEquals(31, barChartData.size());
        assertEquals(2, lineChartData.size());

        // Bar chart checks
        assertBarChartDataPoint(barChartData, 2, 0, 1);
        assertBarChartDataPoint(barChartData, 6, 0, 1);
        assertBarChartDataPoint(barChartData, 14, 1, 3);
        assertBarChartDataPoint(barChartData, 19, 0, 1);
        assertBarChartDataPoint(barChartData, 20, 0, 1);

        // Line chart checks
        Map<?, ?> uniqueScans = lineChartData.getFirst();
        assertEquals("uniqueScans", uniqueScans.get("id"));
        List<Map<?, ?>> uniqueScansData = castList(uniqueScans.get("data"));

        assertLineChartDataPoint(uniqueScansData, 2, 1);
        assertLineChartDataPoint(uniqueScansData, 6, 1);
        assertLineChartDataPoint(uniqueScansData, 14, 3);
        assertLineChartDataPoint(uniqueScansData, 19, 1);
        assertLineChartDataPoint(uniqueScansData, 20, 1);

        Map<?, ?> repeatedScans = lineChartData.get(1);
        assertEquals("repeatedScans", repeatedScans.get("id"));
        List<Map<?, ?>> repeatedScansData = castList(repeatedScans.get("data"));

        assertLineChartDataPoint(repeatedScansData, 2, 0);
        assertLineChartDataPoint(repeatedScansData, 6, 0);
        assertLineChartDataPoint(repeatedScansData, 14, 1);
        assertLineChartDataPoint(repeatedScansData, 19, 0);
        assertLineChartDataPoint(repeatedScansData, 20, 0);
    }

    private void assertWeeklyResponse(Map<?, ?> response) {
        assertEquals(2, response.get("totalUnique"));
        assertEquals(0, response.get("totalRepeated"));
        assertEquals(2, response.get("total"));

        List<Map<?, ?>> barChartData = castList(response.get("barChart"));
        List<Map<?, ?>> lineChartData = castList(response.get("lineChart"));

        // Basic size checks
        assertEquals(7, barChartData.size());
        assertEquals(2, lineChartData.size());

        // Bar chart checks
        for (int day = 0; day <= 6; day++) {
            if (day == 2 || day == 6) {
                assertBarChartDataPoint(barChartData, day, 0, 1);
            } else {
                assertBarChartDataPoint(barChartData, day, 0, 0);
            }
        }

        // Line chart checks
        Map<?, ?> uniqueScans = lineChartData.getFirst();
        assertEquals("uniqueScans", uniqueScans.get("id"));
        List<Map<?, ?>> uniqueScansData = castList(uniqueScans.get("data"));

        for (int day = 0; day <= 6; day++) {
            if (day == 2 || day == 6) {
                assertLineChartDataPoint(uniqueScansData, day, 1);
            } else {
                assertLineChartDataPoint(uniqueScansData, day, 0);
            }
        }

        Map<?, ?> repeatedScans = lineChartData.get(1);
        assertEquals("repeatedScans", repeatedScans.get("id"));
        List<Map<?, ?>> repeatedScansData = castList(repeatedScans.get("data"));

        for (int day = 0; day <= 6; day++) {
            assertLineChartDataPoint(repeatedScansData, day, 0);
        }
    }

    private void assertDailyResponse(Map<?, ?> response) {
        assertEquals(3, response.get("totalUnique"));
        assertEquals(1, response.get("totalRepeated"));
        assertEquals(4, response.get("total"));

        List<Map<?, ?>> barChartData = castList(response.get("barChart"));
        List<Map<?, ?>> lineChartData = castList(response.get("lineChart"));

        // Basic size checks
        assertEquals(24, barChartData.size());
        assertEquals(2, lineChartData.size());

        // Bar chart checks
        for (int hour = 0; hour < 24; hour++) {
            if (hour == 10) {
                assertBarChartDataPoint(barChartData, hour, 1, 1);
            } else if (hour == 12 || hour == 15) {
                assertBarChartDataPoint(barChartData, hour, 0, 1);
            } else {
                assertBarChartDataPoint(barChartData, hour, 0, 0);
            }
        }

        // Line chart checks (uniqueScans)
        Map<?, ?> uniqueScans = lineChartData.getFirst();
        assertEquals("uniqueScans", uniqueScans.get("id"));
        List<Map<?, ?>> uniqueScansData = castList(uniqueScans.get("data"));

        for (int hour = 0; hour < 24; hour++) {
            if (hour == 10 || hour == 12 || hour == 15) {
                assertHourlyLineChartDataPoint(uniqueScansData, hour, 1);
            } else {
                assertHourlyLineChartDataPoint(uniqueScansData, hour, 0);
            }
        }

        // Line chart checks (repeatedScans)
        Map<?, ?> repeatedScans = lineChartData.get(1);
        assertEquals("repeatedScans", repeatedScans.get("id"));
        List<Map<?, ?>> repeatedScansData = castList(repeatedScans.get("data"));

        for (int hour = 0; hour < 24; hour++) {
            if (hour == 10) {
                assertHourlyLineChartDataPoint(repeatedScansData, hour, 1);
            } else {
                assertHourlyLineChartDataPoint(repeatedScansData, hour, 0);
            }
        }
    }

    // ---------------------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private List<Map<?, ?>> castList(Object obj) {
        return (List<Map<?, ?>>) obj;
    }

    private void assertBarChartDataPoint(List<Map<?, ?>> data, int index, int expectedRepeated, int expectedUnique) {
        assertEquals(expectedRepeated, data.get(index).get("repeatedScans"));
        assertEquals(expectedUnique, data.get(index).get("uniqueScans"));
    }

    private void assertLineChartDataPoint(List<Map<?, ?>> data, int x, int y) {
        assertEquals(x + 1, data.get(x).get("x"));
        assertEquals(y, data.get(x).get("y"));
    }

    private void assertHourlyLineChartDataPoint(List<Map<?, ?>> data, int x, int y) {
        assertEquals(x, data.get(x).get("x"));
        assertEquals(y, data.get(x).get("y"));
    }

    // ---------------------------------------------------------------------------------------
    // Yearly-Specific Assertions
    // ---------------------------------------------------------------------------------------

    private void assertYearlyLineChart(Map<?, ?> response) {
        Object lineChartObj = response.get("lineChart");
        if (!(lineChartObj instanceof List<?> lineChartData)) {
            return;
        }

        if (lineChartData.size() < 2) {
            return;
        }

        // uniqueScans
        Map<?, ?> uniqueScans = (Map<?, ?>) lineChartData.getFirst();
        assertEquals("uniqueScans", uniqueScans.get("id"));

        List<Map<?, ?>> uniqueScansData = castList(uniqueScans.get("data"));
        assertEquals(12, uniqueScansData.size());
        assertLineChartDataPoint(uniqueScansData, 0, 5);
        assertLineChartDataPoint(uniqueScansData, 1, 1);
        assertLineChartDataPoint(uniqueScansData, 2, 1);
        assertLineChartDataPoint(uniqueScansData, 3, 2);
        for (int m = 4; m <= 11; m++) {
            assertLineChartDataPoint(uniqueScansData, m, 0);
        }

        // repeatedScans
        Map<?, ?> repeatedScans = (Map<?, ?>) lineChartData.getLast();
        assertEquals("repeatedScans", repeatedScans.get("id"));

        List<Map<?, ?>> repeatedScansData = castList(repeatedScans.get("data"));
        assertEquals(12, repeatedScansData.size());
        assertLineChartDataPoint(repeatedScansData, 0, 3);
        assertLineChartDataPoint(repeatedScansData, 1, 0);
        assertLineChartDataPoint(repeatedScansData, 2, 1);
        assertLineChartDataPoint(repeatedScansData, 3, 1);
        for (int m = 4; m <= 11; m++) {
            assertLineChartDataPoint(repeatedScansData, m, 0);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertYearlyBarChart(Map<?, ?> response) {
        Object barChartObj = response.get("barChart");
        if (!(barChartObj instanceof List)) {
            return;
        }

        List<Map<?, ?>> barChartData = (List<Map<?, ?>>) barChartObj;
        assertBarChartDataPoint(barChartData, 0, 3, 5);
        assertBarChartDataPoint(barChartData, 1, 0, 1);
        assertBarChartDataPoint(barChartData, 2, 1, 1);
        assertBarChartDataPoint(barChartData, 3, 1, 2);
        for (int m = 4; m <= 11; m++) {
            assertBarChartDataPoint(barChartData, m, 0, 0);
        }
    }
}