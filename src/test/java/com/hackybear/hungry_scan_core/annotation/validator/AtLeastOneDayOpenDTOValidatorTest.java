package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AtLeastOneDayOpenDTOValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidOpeningHours_whenValidate_thenNoViolations() {
        Map<DayOfWeek, TimeRange> operatingHours = getValidOpeningHours();
        expectNoViolations(getRestaurant(operatingHours));
    }

    @Test
    void givenInvalidOpeningHours_whenValidate_expectViolations() {
        Map<DayOfWeek, TimeRange> operatingHours = getInvalidOpeningHours();
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.OpeningHours.message}",
                "propertyPath", "settings"
        );
        expectSpecificViolation(getRestaurant(operatingHours), params);
    }

    private Restaurant getRestaurant(Map<DayOfWeek, TimeRange> operatingHours) {
        Restaurant restaurant = new Restaurant();
        restaurant.setOrganizationId(999L);
        restaurant.setName("Restaurant");
        restaurant.setAddress("San Francisco, CA");
        restaurant.setCity("San Francisco");
        restaurant.setPostalCode("1243VA");
        restaurant.setSettings(getSettings(operatingHours));
        restaurant.setCreated(Instant.now());
        return restaurant;
    }

    private Settings getSettings(Map<DayOfWeek, TimeRange> operatingHours) {
        Settings settings = new Settings();
        settings.setOperatingHours(operatingHours);
        return settings;
    }

    private Map<DayOfWeek, TimeRange> getValidOpeningHours() {
        Map<DayOfWeek, TimeRange> openingHours = new HashMap<>();
        for (DayOfWeek value : DayOfWeek.values()) {
            openingHours.put(value, getTimeRange(getLocalTime(8), getLocalTime(16), true));
        }
        return openingHours;
    }

    private Map<DayOfWeek, TimeRange> getInvalidOpeningHours() {
        Map<DayOfWeek, TimeRange> openingHours = new HashMap<>();
        for (DayOfWeek value : DayOfWeek.values()) {
            openingHours.put(value, getTimeRange(getLocalTime(8), getLocalTime(16), false));
        }
        return openingHours;
    }

    private TimeRange getTimeRange(LocalTime startTime, LocalTime endTime, boolean isAvailable) {
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(startTime);
        timeRange.setEndTime(endTime);
        timeRange.setAvailable(isAvailable);
        return timeRange;
    }

    private LocalTime getLocalTime(int hour) {
        return LocalTime.of(hour, 0, 0);
    }
}
