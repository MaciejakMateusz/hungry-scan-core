package com.hackybear.hungry_scan_core.validator;

import com.hackybear.hungry_scan_core.annotation.validator.SizeIfNotEmptyValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SizeIfNotEmptyValidatorTest {

    @Test
    void shouldApproveNullValue() {
        SizeIfNotEmptyValidator validator = new SizeIfNotEmptyValidator();
        assertTrue(validator.isValid(null, mockConstraintValidatorContext()));
    }

    @Test
    void shouldApproveBlankValue() {
        SizeIfNotEmptyValidator validator = new SizeIfNotEmptyValidator();
        assertTrue(validator.isValid("", mockConstraintValidatorContext()));
    }

    @Test
    void shouldApproveValueLengthGreaterThanOrEqualTo8() {
        SizeIfNotEmptyValidator validator = new SizeIfNotEmptyValidator();
        assertTrue(validator.isValid("12345678", mockConstraintValidatorContext()));
    }

    @Test
    void shouldNotApproveValueLengthLessThan8() {
        SizeIfNotEmptyValidator validator = new SizeIfNotEmptyValidator();
        assertFalse(validator.isValid("1234567", mockConstraintValidatorContext()));
    }

    private ConstraintValidatorContext mockConstraintValidatorContext() {
        return Mockito.mock(ConstraintValidatorContext.class);
    }
}