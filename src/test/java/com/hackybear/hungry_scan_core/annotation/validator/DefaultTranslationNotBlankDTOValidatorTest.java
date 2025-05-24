package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DefaultTranslationNotBlankDTOValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList("Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it");
        expectNoViolations(translatables, this::getIngredientDTO);
    }

    @Test
    void givenEmptyDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = Arrays.asList("", null);
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.NotBlank.message}",
                "propertyPath", "name");
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTO, params);
    }

    private IngredientDTO getIngredientDTO(String translation) {
        TranslatableDTO translatable = new TranslatableDTO(1L, translation, null, null, null, null, null);
        return new IngredientDTO(
                1L,
                translatable,
                BigDecimal.valueOf(12),
                true,
                null, null, null, null);
    }
}