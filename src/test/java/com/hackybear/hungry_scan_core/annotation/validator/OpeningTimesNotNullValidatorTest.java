package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.entity.PricePlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
public class OpeningTimesNotNullValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidSettings_whenValidate_thenNoViolations() {
        Restaurant restaurant = getRestaurant(LocalTime.of(10, 0), LocalTime.of(20, 0));
        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenNullOpeningTime_whenValidate_thenExpectViolations() {
        Restaurant restaurant = getRestaurant(null, LocalTime.of(20, 0));
        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);
        assertEquals(1, violations.size());
        ConstraintViolation<Restaurant> violation = violations.iterator().next();
        assertEquals("settings", violation.getPropertyPath().toString());
        assertEquals("Pole nie może być puste", violation.getMessage());
        assertEquals("{jakarta.validation.constraints.NotNull.message}", violation.getMessageTemplate());
    }

    @Test
    void givenNullClosingTime_whenValidate_thenExpectViolations() {
        Restaurant restaurant = getRestaurant(LocalTime.of(8, 0), null);
        Set<ConstraintViolation<Restaurant>> violations = validator.validate(restaurant);
        assertEquals(1, violations.size());
        ConstraintViolation<Restaurant> violation = violations.iterator().next();
        assertEquals("settings", violation.getPropertyPath().toString());
        assertEquals("Pole nie może być puste", violation.getMessage());
        assertEquals("{jakarta.validation.constraints.NotNull.message}", violation.getMessageTemplate());
    }

    private Restaurant getRestaurant(LocalTime openingTime, LocalTime closingTime) {
        Restaurant restaurant = new Restaurant();
        restaurant.setSettings(getSettings(openingTime, closingTime));
        restaurant.setName("test");
        restaurant.setAddress("test");
        restaurant.setPostalCode("12-1234");
        restaurant.setCity("Test");
        restaurant.setPricePlan(new PricePlan());
        restaurant.setCreated(Instant.now());
        return restaurant;
    }

    private Settings getSettings(LocalTime openingTime, LocalTime closingTime) {
        Settings settings = new Settings();
        settings.setOpeningTime(openingTime);
        settings.setClosingTime(closingTime);
        return settings;
    }
}
