package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
public class OpeningClosingTimeValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidOpeningHours_whenValidate_thenNoViolations() {
        Map<DayOfWeek, TimeRange> operatingHours = getValidOpeningHours();
        expectNoViolations(getRestaurant(operatingHours));
    }

    @Test
    void givenInvalidOpeningHours_whenValidate_expectViolations() {
        Map<DayOfWeek, TimeRange> operatingHours = getInvalidOpeningHours();
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.OpeningClosingTime.message}",
                "propertyPath", "settings"
        );
        expectSpecificViolation(getRestaurant(operatingHours), params);
    }

    private RestaurantDTO getRestaurant(Map<DayOfWeek, TimeRange> operatingHours) {
        return new RestaurantDTO(
                null,
                "fakeToken",
                22,
                "Restaurant",
                "San Francisco, CA",
                "1243VA",
                "San Francisco",
                Set.of(), getSettings(operatingHours),
                null,
                null,
                Instant.now(),
                null,
                null,
                null);
    }

    private SettingsDTO getSettings(Map<DayOfWeek, TimeRange> operatingHours) {
        return new SettingsDTO(
                null,
                null,
                operatingHours,
                2L,
                Language.PL,
                2L,
                2L,
                (short) 2,
                true,
                true);
    }

    private Map<DayOfWeek, TimeRange> getValidOpeningHours() {
        Map<DayOfWeek, TimeRange> openingHours = new HashMap<>();
        for (DayOfWeek value : DayOfWeek.values()) {
            openingHours.put(value, getTimeRange(getLocalTime(8), getLocalTime(16)));
        }
        return openingHours;
    }

    private Map<DayOfWeek, TimeRange> getInvalidOpeningHours() {
        Map<DayOfWeek, TimeRange> openingHours = new HashMap<>();
        for (DayOfWeek value : DayOfWeek.values()) {
            openingHours.put(value, getTimeRange(getLocalTime(12), getLocalTime(12)));
        }
        return openingHours;
    }

    private TimeRange getTimeRange(LocalTime startTime, LocalTime endTime) {
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(startTime);
        timeRange.setEndTime(endTime);
        timeRange.setAvailable(true);
        return timeRange;
    }

    private LocalTime getLocalTime(int hour) {
        return LocalTime.of(hour, 0, 0);
    }
}
