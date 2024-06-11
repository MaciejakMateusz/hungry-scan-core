package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.utility.Money;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VariantControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldFindById() throws Exception {
        Variant variant = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", 2, Variant.class);
        assertEquals("Z konfiturą cebulową", variant.getName().getDefaultTranslation());
        assertNull(variant.getName().getTranslationEn());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowForbiddenToShowVariant() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/variants/show", 2, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READ_ONLY"})
    void shouldFindAllByMenuItem() throws Exception {
        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 24, Variant.class);

        assertEquals(3, variants.size());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersist() throws Exception {
        Variant newVariant = createVariant(2, "Wariat", Money.of(12.50));

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 2, Variant.class);

        assertEquals(1, variants.size());
        Variant persistedVariant = variants.get(0);
        assertEquals("Wariat", persistedVariant.getName().getDefaultTranslation());
        assertFalse(persistedVariant.isDefaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersistWithZeroPrice() throws Exception {
        Variant newVariant = createVariant(7, "Wariat", Money.of(0.00));
        newVariant.setDefaultVariant(true);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 7, Variant.class);

        assertEquals(1, variants.size());
        Variant persistedVariant = variants.get(0);
        assertEquals(Money.of(0.00), persistedVariant.getPrice());
        assertTrue(persistedVariant.isDefaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersistMultipleForOneItem() throws Exception {
        Variant newVariant1 = createVariant(9, "Wariat1", Money.of(0.00));
        newVariant1.setDisplayOrder(1);
        Variant newVariant2 = createVariant(9, "Wariat2", Money.of(6.00));
        newVariant2.setDisplayOrder(2);
        newVariant2.setDefaultVariant(false);
        Variant newVariant3 = createVariant(9, "Wariat3", Money.of(9.00));
        newVariant3.setDisplayOrder(7);
        newVariant3.setDefaultVariant(true);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant1);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant2);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant3);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 9, Variant.class);

        Variant persistedVariant1 = variants.get(0);
        Variant persistedVariant2 = variants.get(1);
        Variant persistedVariant3 = variants.get(2);
        assertEquals(3, variants.size());
        assertFalse(persistedVariant1.isDefaultVariant());
        assertEquals(Money.of(6.00), persistedVariant2.getPrice());
        assertEquals("Wariat3", persistedVariant3.getName().getDefaultTranslation());
        assertEquals(3, persistedVariant3.getDisplayOrder());
        assertTrue(persistedVariant3.isDefaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateDefaultFieldInExisting() throws Exception {
        Variant existingVariant = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", 4, Variant.class);
        existingVariant.setDefaultVariant(true);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", existingVariant);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, Variant.class);
        assertEquals(3, variants.size());

        Variant variant1 = variants.get(0);
        Variant variant2 = variants.get(1);
        Variant variant3 = variants.get(2);
        assertFalse(variant1.isDefaultVariant());
        assertTrue(variant2.isDefaultVariant());
        assertFalse(variant3.isDefaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSwitchDefaultToNextVariant() throws Exception {
        Variant existingVariant = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", 3, Variant.class);
        existingVariant.setDefaultVariant(false);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", existingVariant);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, Variant.class);
        assertEquals(3, variants.size());

        Variant variant1 = variants.get(0);
        Variant variant2 = variants.get(1);
        Variant variant3 = variants.get(2);
        assertFalse(variant1.isDefaultVariant());
        assertFalse(variant2.isDefaultVariant());
        assertFalse(variant3.isDefaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotPersistWithInvalidName() throws Exception {
        Variant newVariant = createVariant(4, "", Money.of(12.50));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", newVariant);
        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));

        newVariant.setName(null);
        errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", newVariant);
        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotPersistWithInvalidPrice() throws Exception {
        Variant newVariant = createVariant(12, "Wariat", Money.of(-44.00));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", newVariant);
        assertEquals(1, errors.size());
        assertEquals("Cena musi być równa lub większa od 0.00", errors.get("price"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowForbiddenToAddVariant() throws Exception {
        Variant variant = createVariant(12, "Not hehe", Money.of(5.50));
        apiRequestUtils.postAndExpect("/api/cms/variants/add", variant, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccess() throws Exception {
        apiRequestUtils.postAndExpectUnauthorized("/api/cms/variants/show", 4);
        apiRequestUtils.postAndExpectUnauthorized("/api/cms/variants/item", 14);
        Variant variant = createVariant(12, "Not hehe", Money.of(5.50));
        apiRequestUtils.postAndExpectUnauthorized("/api/cms/variants/add", variant);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldRemoveVariant() throws Exception {
        Variant variant =
                apiRequestUtils.postObjectExpect200("/api/cms/variants/show", 4, Variant.class);
        assertEquals("Średnia", variant.getName().getDefaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/variants/delete", 4);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/variants/show", 4, status().isBadRequest());
        assertEquals("Wariant dania z podanym ID = 4 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrderAfterRemoval() throws Exception {
        Variant variant =
                apiRequestUtils.postObjectExpect200("/api/cms/variants/show", 4, Variant.class);
        assertEquals("Średnia", variant.getName().getDefaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/variants/delete", 4);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, Variant.class);
        assertEquals(2, variants.size());
        assertEquals("Duża", variants.get(1).getName().getDefaultTranslation());
    }

    private Variant createVariant(Integer menuItemId,
                                  String name,
                                  BigDecimal price) {
        Variant variant = new Variant();
        variant.setMenuItem(getMenuItem(menuItemId));
        variant.setName(getDefaultTranslation(name));
        variant.setPrice(price);
        variant.setAvailable(true);
        return variant;
    }

    private MenuItem getMenuItem(Integer id) {
        return menuItemRepository.findById(id).orElseThrow();
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}