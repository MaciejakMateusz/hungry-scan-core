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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals("Wariat", variants.get(0).getName().getDefaultTranslation());
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
        assertEquals(Money.of(0.00), variants.get(0).getPrice());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersistMultipleForOneItem() throws Exception {
        Variant newVariant1 = createVariant(9, "Wariat1", Money.of(0.00));
        newVariant1.setDefaultVariant(true);
        newVariant1.setDisplayOrder(1);
        Variant newVariant2 = createVariant(9, "Wariat2", Money.of(6.00));
        newVariant2.setDisplayOrder(2);
        Variant newVariant3 = createVariant(9, "Wariat3", Money.of(9.00));
        newVariant3.setDisplayOrder(7);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant1);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant2);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", newVariant3);

        List<Variant> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 9, Variant.class);

        assertEquals(3, variants.size());
        assertTrue(variants.get(0).isDefaultVariant());
        assertEquals(Money.of(6.00), variants.get(1).getPrice());
        assertEquals("Wariat3", variants.get(2).getName().getDefaultTranslation());
        assertEquals(3, variants.get(2).getDisplayOrder());
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