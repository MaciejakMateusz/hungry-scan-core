package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Translatable;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
class AnyTranslationNotBlankValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, aromatic curries, fresh seafood, "
                        + "and hearty vegetarian delights—each dish thoughtfully seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it"
        );
        expectNoViolations(translatables, AnyTranslationNotBlankValidatorTest.this::getCategoryWithPl);
    }

    /**
     * If the Translatable itself is null, we treat that as “no translation to check” and thus no violation.
     */
    @Test
    void givenEmptyDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalid = Arrays.asList("", null);
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.AnyTranslationNotBlank.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalid, AnyTranslationNotBlankValidatorTest.this::getCategoryWithPl, params);
    }

    /**
     * If the Translatable itself is null, we treat that as “no translation to check” and thus no violation.
     */
    @Test
    void givenTranslatableNull_whenValidate_thenNoValidatorViolation() {
        Category cat = getCategory(null);

        Set<ConstraintViolation<Category>> violations = validator.validate(cat);

        assertTrue(violations.stream().noneMatch(v -> v.getMessageTemplate()
                .equals("{jakarta.validation.constraints.AnyTranslationNotBlank.message}")));
        assertEquals(1, violations.size());
        assertEquals("{jakarta.validation.constraints.NotNull.message}",
                violations.iterator().next().getMessageTemplate());
    }

    /**
     * Test that *any* single language field non‐blank yields NO violation.
     */
    @ParameterizedTest(name = "non-blank in {0} → no violations")
    @EnumSource(Language.class)
    void givenNonBlankInAnyLanguage_whenValidate_thenNoViolations(Language lang) {
        List<String> samples = Arrays.asList("X", "Hello", "123", "!@#");
        Function<String, Category> factory = languageToFactory(lang);
        expectNoViolations(samples, factory);
    }

    /**
     * Test that if *all* fields are blank or null, we get exactly one violation on 'name'.
     */
    @Test
    void givenAllTranslationsBlankOrNull_whenValidate_thenExpectViolations() {
        Translatable allNull = new Translatable();
        Translatable allBlank = new Translatable();
        allBlank.setPl("");
        allBlank.setEn("");
        allBlank.setFr("");
        allBlank.setDe("");
        allBlank.setEs("");
        allBlank.setUk("");

        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.AnyTranslationNotBlank.message}",
                "propertyPath", "name"
        );

        expectViolation(
                List.of(allNull, allBlank),
                this::getCategory,
                params
        );
    }

    private Category getCategoryWithPl(String txt) {
        return buildCategory(t -> t.setPl(txt));
    }

    private Category getCategoryWithEn(String txt) {
        return buildCategory(t -> t.setEn(txt));
    }

    private Category getCategoryWithFr(String txt) {
        return buildCategory(t -> t.setFr(txt));
    }

    private Category getCategoryWithDe(String txt) {
        return buildCategory(t -> t.setDe(txt));
    }

    private Category getCategoryWithEs(String txt) {
        return buildCategory(t -> t.setEs(txt));
    }

    private Category getCategoryWithUk(String txt) {
        return buildCategory(t -> t.setUk(txt));
    }

    private Category getCategory(Translatable t) {
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category buildCategory(Consumer<Translatable> setter) {
        Translatable t = new Translatable();
        setter.accept(t);
        return getCategory(t);
    }

    private Function<String, Category> languageToFactory(Language lang) {
        return switch (lang) {
            case PL -> this::getCategoryWithPl;
            case EN -> this::getCategoryWithEn;
            case FR -> this::getCategoryWithFr;
            case DE -> this::getCategoryWithDe;
            case ES -> this::getCategoryWithEs;
            case UK -> this::getCategoryWithUk;
        };
    }

    private enum Language {PL, EN, FR, DE, ES, UK}

    private void expectViolation(List<Translatable> inputs,
                                 Function<Translatable, Category> provider,
                                 Map<String, String> params) {
        inputs.forEach(t -> {
            Category cat = provider.apply(t);
            expectSpecificViolation(
                    List.of("dummy"),
                    __ -> cat,
                    params
            );
        });
    }

}
