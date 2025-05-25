package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Translatable;
import org.junit.jupiter.api.Test;
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
public class LimitTranslationsLengthValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidDefaultTranslation_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, "
                        + "aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully "
                        + "seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it"
        );
        expectNoViolations(translatables, this::getCategoryWithDefaultTranslation);
    }

    @Test
    void givenInvalidDefaultTranslation_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, "
                        + "aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully "
                        + "seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithDefaultTranslation, params);
    }

    @Test
    void givenValidTranslationEn_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Meat", "Salads", "A", "abB!@#", "123123",
                "Discover mouthwatering mains crafted with the finest ingredients—succulent steaks, "
                        + "aromatic curries, fresh seafood, and hearty vegetarian delights—each dish thoughtfully "
                        + "seasoned to tantalize taste buds and offer unforgettable flavor experiences. Savor it"
        );
        expectNoViolations(translatables, this::getCategoryWithTranslationEn);
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
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationEn, params);
    }

    @Test
    void givenValidTranslationFr_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Viande", "Salades", "Une", "abc#@!", "456789",
                "Savourez nos plats principaux succulents préparés avec les ingrédients les plus fins—des steaks " +
                        "tendres, des currys parfumés, des fruits de mer frais et des délices végétariens " +
                        "chaleureux—chaque plat étant soigneusement assaisonné pour titiller les papill"
        );
        expectNoViolations(translatables, this::getCategoryWithTranslationFr);
    }

    @Test
    void givenInvalidTranslationFr_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Savourez nos plats principaux succulents préparés avec les ingrédients les plus fins—"
                        + "des steaks tendres, des currys parfumés, des fruits de mer frais et des délices végétariens "
                        + "chaleureux—chaque plat étant soigneusement assaisonné pour titiller les papilles et offrir "
                        + "une expérience de saveur inoubliable."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationFr, params);
    }

    @Test
    void givenValidTranslationDe_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Fleisch", "Salate", "Ein", "xyz!@#", "789012",
                "Genießen Sie unsere köstlichen Hauptgerichte, zubereitet mit den feinsten Zutaten—zarte Steaks, " +
                        "aromatische Currys, frische Meeresfrüchte und herzhafte vegetarische Köstlichkeiten—jedes " +
                        "Gericht sorgsam gewürzt, um den Gaumen zu kitzeln und unvergessliche."
        );
        expectNoViolations(translatables, this::getCategoryWithTranslationDe);
    }

    @Test
    void givenInvalidTranslationDe_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Genießen Sie unsere köstlichen Hauptgerichte, zubereitet mit den feinsten Zutaten—"
                        + "zarte Steaks, aromatische Currys, frische Meeresfrüchte und herzhafte vegetarische Köstlichkeiten—"
                        + "jedes Gericht sorgsam gewürzt, um den Gaumen zu kitzeln und unvergessliche " +
                        "Geschmackserlebnisse zu bieten."

        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationDe, params);
    }

    @Test
    void givenValidTranslationEs_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "Carne", "Ensaladas", "Uno", "¡Hola123!", "345678",
                "Descubra platos principales deliciosos elaborados con los ingredientes más finos—jugosos filetes, " +
                        "curris aromáticos, mariscos frescos y suculentas delicias vegetarianas—cada plato " +
                        "cuidadosamente sazonado para deleitar el paladar y ofrecer experiencias de."
        );
        expectNoViolations(translatables, this::getCategoryWithTranslationEs);
    }

    @Test
    void givenInvalidTranslationEs_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Descubra platos principales deliciosos elaborados con los ingredientes más finos—"
                        + "jugosos filetes, curris aromáticos, mariscos frescos y suculentas delicias vegetarianas—"
                        + "cada plato cuidadosamente sazonado para deleitar el paladar y ofrecer experiencias " +
                        "de sabor inolvidables."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationEs, params);
    }

    @Test
    void givenValidTranslationUk_whenValidate_thenNoViolations() {
        List<String> translatables = Arrays.asList(
                "М’ясо", "Салати", "А", "тест123", "678901",
                "Скуштуйте наші апетитні основні страви, приготовані з найкращих інгредієнтів—соковиті " +
                        "стейки, ароматні каррі, свіжі морепродукти та ситні вегетаріанські смаколики—кожна " +
                        "страва ретельно приправлена, щоб задовольнити смакові рецептори та подарувати незабутн"
        );
        expectNoViolations(translatables, this::getCategoryWithTranslationUk);
    }

    @Test
    void givenInvalidTranslationUk_whenValidate_thenExpectViolations() {
        List<String> invalidTranslatables = List.of(
                "Скуштуйте наші апетитні основні страви, приготовані з найкращих інгредієнтів—"
                        + "соковиті стейки, ароматні каррі, свіжі морепродукти та ситні вегетаріанські смаколики—"
                        + "кожна страва ретельно приправлена, щоб задовольнити смакові рецептори та подарувати " +
                        "незабутні смакові враження."
        );
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.LimitTranslationsLength.message}",
                "propertyPath", "name"
        );
        expectSpecificViolation(invalidTranslatables, this::getCategoryWithTranslationUk, params);
    }

    private Category getCategoryWithDefaultTranslation(String translation) {
        Translatable t = new Translatable();
        t.setPl(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category getCategoryWithTranslationEn(String translation) {
        Translatable t = new Translatable();
        t.setPl("Default");
        t.setEn(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category getCategoryWithTranslationFr(String translation) {
        Translatable t = new Translatable();
        t.setPl("Default");
        t.setFr(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category getCategoryWithTranslationDe(String translation) {
        Translatable t = new Translatable();
        t.setPl("Default");
        t.setDe(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category getCategoryWithTranslationEs(String translation) {
        Translatable t = new Translatable();
        t.setPl("Default");
        t.setEs(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }

    private Category getCategoryWithTranslationUk(String translation) {
        Translatable t = new Translatable();
        t.setPl("Default");
        t.setUk(translation);
        Category c = new Category();
        c.setMenu(new Menu());
        c.setName(t);
        c.setDisplayOrder(1);
        return c;
    }
}