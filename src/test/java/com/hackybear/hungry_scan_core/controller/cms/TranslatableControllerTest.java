package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TranslatableControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldFindAll() throws Exception {
        Map<String, List<Translatable>> translatables =
                apiRequestUtils.fetchTranslatablesMap("/api/cms/translatable");
        assertEquals(7, translatables.size());

        List<Translatable> menuItemsTransl = translatables.get("menuItems");
        List<Translatable> ingredientsTransl = translatables.get("ingredients");
        List<Translatable> categoriesTransl = translatables.get("categories");
        List<Translatable> variantsTransl = translatables.get("variants");
        List<Translatable> zonesTransl = translatables.get("zones");
        List<Translatable> allergensTransl = translatables.get("allergens");
        List<Translatable> labelsTransl = translatables.get("labels");

        assertEquals(66, menuItemsTransl.size());
        assertEquals("Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym", menuItemsTransl.get(5).getDefaultTranslation());
        assertEquals(27, ingredientsTransl.size());
        assertEquals("Szpinak", ingredientsTransl.get(17).getDefaultTranslation());
        assertEquals(9, categoriesTransl.size());
        assertEquals("Pizza", categoriesTransl.get(4).getDefaultTranslation());
        assertEquals(17, variantsTransl.size());
        assertEquals("Z konfiturą cebulową", variantsTransl.get(1).getDefaultTranslation());
        assertEquals(4, zonesTransl.size());
        assertEquals("VIP Lounge", zonesTransl.get(3).getTranslationEn());
        assertEquals(28, allergensTransl.size());
        assertEquals("Sulfur dioxide", allergensTransl.get(22).getTranslationEn());
        assertEquals(6, labelsTransl.size());
        assertEquals("Gluten free", labelsTransl.get(0).getTranslationEn());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSaveAll() throws Exception {
        Map<String, List<Translatable>> translatables =
                apiRequestUtils.fetchTranslatablesMap("/api/cms/translatable");
        translatables.get("zones").get(2).setTranslationEn("High On Life");
        translatables.get("categories").get(4).setDefaultTranslation("Pitca");

        apiRequestUtils.postAndExpect200("/api/cms/translatable", translatables);

        Map<String, List<Translatable>> updatedTranslatables =
                apiRequestUtils.fetchTranslatablesMap("/api/cms/translatable");

        assertEquals(7, updatedTranslatables.size());
        assertEquals("High On Life", translatables.get("zones").get(2).getTranslationEn());
        assertEquals("Pitca", translatables.get("categories").get(4).getDefaultTranslation());
    }

}