package com.hackybear.hungry_scan_core.controller.statistics;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    private static final int YEAR_2024 = 2024;

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Order(1)
    @Sql({"/data-h2.sql", "/test-packs/qr-scans.sql", "/test-packs/menu-item-view-events.sql", "/h2/functions.sql"})
    @Test
    void init() {
        log.info("Initializing H2 database: inserting QR scans data, view events and SQL functions...");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetYearlyScanStats() throws Exception {
        Map<String, Object> params = Map.of(
                "year", YEAR_2024
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/year/scans",
                params,
                status().isOk()
        );

        assertYearlyScansResponse(response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetYearlyMenuItemViewsStats() throws Exception {
        Map<String, Object> params = Map.of("menuId", 1L, "year", YEAR_2024);
        Set<MenuItemViewCountDTO> response =
                apiRequestUtils.postAndGetSet(
                        "/api/stats/dashboard/year/menu-item-views",
                        params,
                        MenuItemViewCountDTO.class);
        assertYearlyMenuItemViewsResponse(response);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetMonthlyScanStats() throws Exception {
        Map<String, Object> params = Map.of(
                "year", YEAR_2024,
                "month", 1
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/month/scans",
                params,
                status().isOk()
        );

        assertMonthlyScansResponse(response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetMonthlyMenuItemViewsStats() throws Exception {
        Map<String, Object> params = Map.of("menuId", 1L, "month", 1, "year", YEAR_2024);
        Set<MenuItemViewCountDTO> response =
                apiRequestUtils.postAndGetSet(
                        "/api/stats/dashboard/month/menu-item-views",
                        params,
                        MenuItemViewCountDTO.class);
        assertMonthlyMenuItemViewsResponse(response);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetWeeklyScanStats() throws Exception {
        Map<String, Object> params = Map.of(
                "year", YEAR_2024,
                "week", 1
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/week/scans",
                params,
                status().isOk()
        );

        assertWeeklyScansResponse(response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetWeeklyMenuItemViewsStats() throws Exception {
        Map<String, Object> params = Map.of("menuId", 1L, "week", 1, "year", YEAR_2024);
        Set<MenuItemViewCountDTO> response =
                apiRequestUtils.postAndGetSet(
                        "/api/stats/dashboard/week/menu-item-views",
                        params,
                        MenuItemViewCountDTO.class);
        assertWeeklyMenuItemViewsResponse(response);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetDailyScanStats() throws Exception {
        Map<String, Object> params = Map.of(
                "day", "2024-01-15T00:00:00Z"
        );

        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/stats/dashboard/day/scans",
                params,
                status().isOk()
        );

        assertDailyScansResponse(response);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldGetDailyMenuItemViewsStats() throws Exception {
        Map<String, Object> params = Map.of("menuId", 1L, "day", "2024-03-31T00:00:00Z");
        Set<MenuItemViewCountDTO> response =
                apiRequestUtils.postAndGetSet(
                        "/api/stats/dashboard/day/menu-item-views",
                        params,
                        MenuItemViewCountDTO.class);
        assertDailyMenuItemViewsResponse(response);
    }

    // ---------------------------------------------------------------------------------------
    // Assertion Methods
    // ---------------------------------------------------------------------------------------

    private void assertYearlyScansResponse(Map<?, ?> response) {
        assertEquals(9, response.get("totalUnique"));
        assertEquals(5, response.get("totalRepeated"));
        assertEquals(14, response.get("total"));

        assertYearlyScansLineChart(response);
        assertYearlyScansBarChart(response);
    }

    private void assertYearlyMenuItemViewsResponse(Set<MenuItemViewCountDTO> response) {
        assertFalse(response.isEmpty());
        for (MenuItemViewCountDTO itemViewCountDTO : response) {
            switch (itemViewCountDTO.id().toString()) {
                case "4", "5", "7", "8" -> assertEquals(1, itemViewCountDTO.viewsCount());
                case "6", "9" -> assertEquals(2, itemViewCountDTO.viewsCount());
                case "2", "3" -> assertEquals(3, itemViewCountDTO.viewsCount());
                case "1" -> assertEquals(6, itemViewCountDTO.viewsCount());
            }
        }
    }

    private void assertMonthlyScansResponse(Map<?, ?> response) {
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

    private void assertMonthlyMenuItemViewsResponse(Set<MenuItemViewCountDTO> response) {
        assertFalse(response.isEmpty());
        for (MenuItemViewCountDTO itemViewCountDTO : response) {
            switch (itemViewCountDTO.id().toString()) {
                case "3", "4", "5", "6" -> assertEquals(1, itemViewCountDTO.viewsCount());
                case "2" -> assertEquals(2, itemViewCountDTO.viewsCount());
                case "1" -> assertEquals(4, itemViewCountDTO.viewsCount());
            }
        }
    }

    private void assertWeeklyScansResponse(Map<?, ?> response) {
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

    private void assertWeeklyMenuItemViewsResponse(Set<MenuItemViewCountDTO> response) {
        assertFalse(response.isEmpty());
        for (MenuItemViewCountDTO itemViewCountDTO : response) {
            switch (itemViewCountDTO.id().toString()) {
                case "1" -> assertEquals(2, itemViewCountDTO.viewsCount());
                case "2", "3", "4" -> assertEquals(1, itemViewCountDTO.viewsCount());
            }
        }
    }

    private void assertDailyScansResponse(Map<?, ?> response) {
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

    private void assertDailyMenuItemViewsResponse(Set<MenuItemViewCountDTO> response) {
        assertFalse(response.isEmpty());
        for (MenuItemViewCountDTO itemViewCountDTO : response) {
            switch (itemViewCountDTO.id().toString()) {
                case "1" -> assertEquals(2, itemViewCountDTO.viewsCount());
                case "6" -> assertEquals(1, itemViewCountDTO.viewsCount());
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

    private void assertYearlyScansLineChart(Map<?, ?> response) {
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
    private void assertYearlyScansBarChart(Map<?, ?> response) {
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