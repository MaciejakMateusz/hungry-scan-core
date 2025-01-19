package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LimitTranslationsLengthValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList("Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it");
        expectNoViolations(translatables, this::getCategoryWithDefaultTranslation);
    }

    @Test
    void givenValidTranslationEn_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList("Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it");
        expectNoViolations(translatables, this::getCategoryWithTranslationEn);
    }

    @Test
    void givenInvalidDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of("Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it.");
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name");
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithDefaultTranslation, params);
    }

    @Test
    void givenInvalidTranslationEn_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of("Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it.");
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name");
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationEn, params);
    }

    private Category getCategoryWithDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        Category category = new Category();
        category.setMenuId(1L);
        category.setName(translatable);
        category.setDisplayOrder(1);
        return category;
    }

    private Category getCategoryWithTranslationEn(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("Default");
        translatable.setTranslationEn(translation);
        Category category = new Category();
        category.setMenuId(1L);
        category.setName(translatable);
        category.setDisplayOrder(1);
        return category;
    }
}
