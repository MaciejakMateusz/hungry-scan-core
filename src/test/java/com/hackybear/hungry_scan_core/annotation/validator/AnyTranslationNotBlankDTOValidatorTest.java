package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AnyTranslationNotBlankDTOValidatorTest extends ValidatorTestBase {

    /**
     * A non‐blank default (PL) translation should always pass.
     */
    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, "
                        + "fresh seafood, and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds."
        );
        expectNoViolations(translatables, this::getIngredientDTOWithPl);
    }

    /**
     * Empty or null default (PL) translation should trigger our custom constraint.
     */
    @Test
    void givenEmptyDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalid = Arrays.asList("", null);
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.AnyTranslationNotBlank.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalid, this::getIngredientDTOWithPl, params);
    }

    /**
     * If the Translatable itself is null, we treat that as “no translation to check” and thus no violation.
     */
    @Test
    void givenTranslatableNull_whenValidate_thenNoValidatorViolation() {
        IngredientDTO cat = getIngredientDTONull();

        Set<ConstraintViolation<IngredientDTO>> violations = validator.validate(cat);

        assertTrue(violations.stream().noneMatch(v -> v.getMessageTemplate()
                .equals("{jakarta.validation.constraints.AnyTranslationNotBlank.message}")));
        assertEquals(1, violations.size());
        assertEquals("{jakarta.validation.constraints.NotNull.message}",
                violations.iterator().next().getMessageTemplate());
    }

    /**
     * Any single language non‐blank (in PL, EN, FR, DE, ES, or UK) should pass.
     */
    @ParameterizedTest(name = "non‐blank in {0} → no violations")
    @EnumSource(Language.class)
    void givenNonBlankInAnyLanguage_whenValidate_thenNoViolations(Language lang) {
        List<String> samples = Arrays.asList("X", "Hello", "123", "!@#");
        Function<String, IngredientDTO> factory = languageToFactory(lang);
        expectNoViolations(samples, factory);
    }

    /**
     * If *all* translations are either blank or null, we must get exactly one violation on 'name'.
     */
    @Test
    void givenAllTranslationsBlankOrNull_whenValidate_thenExpectViolations() {
        TranslatableDTO allNull = new TranslatableDTO(1L, null, null, null, null, null, null);
        TranslatableDTO allBlank = new TranslatableDTO(1L, "", "", "", "", "", "");

        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.AnyTranslationNotBlank.message}",
                "propertyPath", "name"
        );

        expectViolationDTO(
                List.of(allNull, allBlank),
                this::getIngredientDTOFromTranslatable,
                params
        );
    }

    private IngredientDTO getIngredientDTOWithPl(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, txt, null, null, null, null, null));
    }

    private IngredientDTO getIngredientDTOWithEn(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, null, txt, null, null, null, null));
    }

    private IngredientDTO getIngredientDTOWithFr(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, null, null, txt, null, null, null));
    }

    private IngredientDTO getIngredientDTOWithDe(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, null, null, null, txt, null, null));
    }

    private IngredientDTO getIngredientDTOWithEs(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, null, null, null, null, txt, null));
    }

    private IngredientDTO getIngredientDTOWithUk(String txt) {
        return buildIngredientDTO(new TranslatableDTO(1L, null, null, null, null, null, txt));
    }

    private IngredientDTO getIngredientDTONull() {
        return new IngredientDTO(
                1L,
                null,
                BigDecimal.valueOf(12),
                true,
                null, null, null, null
        );
    }

    private IngredientDTO getIngredientDTOFromTranslatable(TranslatableDTO t) {
        return buildIngredientDTO(t);
    }

    private IngredientDTO buildIngredientDTO(TranslatableDTO t) {
        return new IngredientDTO(
                1L,
                t,
                BigDecimal.valueOf(12),
                true,
                null, null, null, null
        );
    }

    private Function<String, IngredientDTO> languageToFactory(Language lang) {
        return switch (lang) {
            case PL -> this::getIngredientDTOWithPl;
            case EN -> this::getIngredientDTOWithEn;
            case FR -> this::getIngredientDTOWithFr;
            case DE -> this::getIngredientDTOWithDe;
            case ES -> this::getIngredientDTOWithEs;
            case UK -> this::getIngredientDTOWithUk;
        };
    }

    private enum Language {PL, EN, FR, DE, ES, UK}

    private void expectViolationDTO(List<TranslatableDTO> inputs,
                                    Function<TranslatableDTO, IngredientDTO> provider,
                                    Map<String, String> params) {
        inputs.forEach(t -> {
            IngredientDTO dto = provider.apply(t);
            expectSpecificViolation(
                    List.of("dummy"),
                    __ -> dto,
                    params
            );
        });
    }
}
