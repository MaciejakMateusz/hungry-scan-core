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
public class LimitTranslationsLengthDTOValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Mięso", "Sałatki", "A", "abB!@#", "123123",
                "Odkryj soczyste dania główne przygotowane z najlepszych składników— soczyste steki, aromatyczne curry, " +
                        "świeże owoce morza i obfite wegetariańskie przysmaki— każde danie starannie doprawione, " +
                        "aby rozbudzić kubki smakowe i zapewnić niezapomniane doznania..."
        );
        expectNoViolations(translatables, this::getIngredientDTOWithDefaultTranslation);
    }

    @Test
    void givenInvalidDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Odkryj soczyste dania główne przygotowane z najfiniejszych składników—"
                        + " soczyste steki, aromatyczne curry, świeże owoce morza i obfite wegetariańskie przysmaki—"
                        + " każde danie starannie doprawione, aby rozbudzić kubki smakowe i zapewnić niezapomniane doznania smakowe."
                        + " Dodatkowy tekst, aby przekroczyć limit 255 znaków."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithDefaultTranslation, params);
    }

    @Test
    void givenValidTranslationEn_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, "
                        + "aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully "
                        + "seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it"
        );
        expectNoViolations(translatables, this::getIngredientDTOWithTranslationEn);
    }

    @Test
    void givenInvalidTranslationEn_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, "
                        + "aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully "
                        + "seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithTranslationEn, params);
    }

    @Test
    void givenValidTranslationFr_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Viande", "Salades", "Une", "abc#@!", "456789",
                "Savourez nos plats principaux succulents préparés avec les ingrédients les plus fins—des steaks " +
                        "tendres, des currys parfumés, des fruits de mer frais et des délices végétariens " +
                        "chaleureux—chaque plat étant soigneusement assaisonné pour titiller les papill"
        );
        expectNoViolations(translatables, this::getIngredientDTOWithTranslationFr);
    }

    @Test
    void givenInvalidTranslationFr_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Savourez nos plats principaux succulents préparés avec les ingrédients les plus fins—"
                        + " des steaks tendres, des currys parfumés, des fruits de mer frais et des délices végétariens chaleureux—"
                        + " chaque plat étant soigneusement assaisonné pour titiller les papilles et offrir une expérience savoureuse inoubliable."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithTranslationFr, params);
    }

    @Test
    void givenValidTranslationDe_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Fleisch", "Salate", "Ein", "xyz!@#", "789012",
                "Genießen Sie unsere köstlichen Hauptgerichte, zubereitet mit den feinsten Zutaten—zarte Steaks, " +
                        "aromatische Currys, frische Meeresfrüchte und herzhafte vegetarische Köstlichkeiten—jedes " +
                        "Gericht sorgsam gewürzt, um den Gaumen zu kitzeln und unvergessliche."
        );
        expectNoViolations(translatables, this::getIngredientDTOWithTranslationDe);
    }

    @Test
    void givenInvalidTranslationDe_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Genießen Sie unsere köstlichen Hauptgerichte, zubereitet mit den feinsten Zutaten—"
                        + " zarte Steaks, aromatische Currys, frische Meeresfrüchte und herzhafte vegetarische Köstlichkeiten—"
                        + " jedes Gericht sorgsam gewürzt, um den Gaumen zu verwöhnen und unvergessliche Geschmackserlebnisse zu bieten."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithTranslationDe, params);
    }

    @Test
    void givenValidTranslationEs_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Carne", "Ensaladas", "Uno", "¡Hola123!", "345678",
                "Descubra platos principales deliciosos elaborados con los ingredientes más finos—jugosos filetes, " +
                        "curris aromáticos, mariscos frescos y suculentas delicias vegetarianas—cada plato " +
                        "cuidadosamente sazonado para deleitar el paladar y ofrecer experiencias de."
        );
        expectNoViolations(translatables, this::getIngredientDTOWithTranslationEs);
    }

    @Test
    void givenInvalidTranslationEs_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Descubra platos principales deliciosos elaborados con los ingredientes más finos—"
                        + " jugosos filetes, curris aromáticos, mariscos frescos y suculentas delicias vegetarianas—"
                        + " cada plato cuidadosamente sazonado para deleitar el paladar y ofrecer experiencias "
                        + "de sabor inolvidables."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithTranslationEs, params);
    }

    @Test
    void givenValidTranslationUk_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "М’ясо", "Салати", "А", "тест123", "678901",
                "Скуштуйте наші апетитні основні страви, приготовані з найкращих інгредієнтів—соковиті " +
                        "стейки, ароматні каррі, свіжі морепродукти та ситні вегетаріанські смаколики—кожна " +
                        "страва ретельно приправлена, щоб задовольнити смакові рецептори та подарувати незабутн"
        );
        expectNoViolations(translatables, this::getIngredientDTOWithTranslationUk);
    }

    @Test
    void givenInvalidTranslationUk_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Скуштуйте наші апетитні основні страви, приготовані з найкращих інгредієнтів—"
                        + " соковиті стейки, ароматні каррі, свіжі морепродукти та ситні вегетаріанські смаколики—"
                        + " кожна страва ретельно приправлена, щоб подарувати незабутні смакові враження."
                        + " Додатковий текст, щоб перевищити обмеження в 255 символів."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getIngredientDTOWithTranslationUk, params);
    }

    private IngredientDTO getIngredientDTOWithDefaultTranslation(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, translation, null, null, null, null, null);
        return buildIngredientDTO(t);
    }

    private IngredientDTO getIngredientDTOWithTranslationEn(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, "Default", translation, null, null, null, null);
        return buildIngredientDTO(t);
    }

    private IngredientDTO getIngredientDTOWithTranslationFr(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, "Default", null, translation, null, null, null);
        return buildIngredientDTO(t);
    }

    private IngredientDTO getIngredientDTOWithTranslationDe(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, "Default", null, null, translation, null, null);
        return buildIngredientDTO(t);
    }

    private IngredientDTO getIngredientDTOWithTranslationEs(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, "Default", null, null, null, translation, null);
        return buildIngredientDTO(t);
    }

    private IngredientDTO getIngredientDTOWithTranslationUk(String translation) {
        TranslatableDTO t = new TranslatableDTO(1L, "Default", null, null, null, null, translation);
        return buildIngredientDTO(t);
    }

    private IngredientDTO buildIngredientDTO(TranslatableDTO name) {
        return new IngredientDTO(
                1L,
                name,
                BigDecimal.valueOf(12),
                true,
                null, null, null, null
        );
    }
}