package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.VariantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.VariantMapper;
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
    private VariantMapper variantMapper;
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
        VariantDTO variantDTO = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", 2, VariantDTO.class);
        assertEquals("Z konfiturą cebulową", variantDTO.name().defaultTranslation());
        assertNull(variantDTO.name().translationEn());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowForbiddenToShowVariant() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/variants/show", 2, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READ_ONLY"})
    void shouldFindAllByMenuItem() throws Exception {
        List<VariantDTO> variants = getAllByMenuItemId(24L);

        assertEquals(3, variants.size());
        assertEquals("Mała", variants.getFirst().name().defaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersist() throws Exception {
        Variant newVariant = createVariant(2L, "Wariat", Money.of(12.50));
        VariantDTO variantDTO = variantMapper.toDTO(newVariant);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", variantDTO);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 2, VariantDTO.class);

        assertEquals(1, variants.size());
        VariantDTO persistedVariant = variants.getFirst();
        assertEquals("Wariat", persistedVariant.name().defaultTranslation());
        assertFalse(persistedVariant.defaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersistWithZeroPrice() throws Exception {
        Variant newVariant = createVariant(7L, "Wariat", Money.of(0.00));
        newVariant.setDefaultVariant(true);
        VariantDTO variantDTO = variantMapper.toDTO(newVariant);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", variantDTO);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 7, VariantDTO.class);

        assertEquals(1, variants.size());
        VariantDTO persistedVariant = variants.getFirst();
        assertEquals(Money.of(0.00), persistedVariant.price());
        assertTrue(persistedVariant.defaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Transactional
    @Rollback
    void shouldPersistMultipleForOneItem() throws Exception {
        Variant newVariant1 = createVariant(9L, "Wariat1", Money.of(0.00));
        VariantDTO variantDTO1 = variantMapper.toDTO(newVariant1);

        Variant newVariant2 = createVariant(9L, "Wariat2", Money.of(6.00));
        newVariant2.setDefaultVariant(false);
        VariantDTO variantDTO2 = variantMapper.toDTO(newVariant2);

        Variant newVariant3 = createVariant(9L, "Wariat3", Money.of(9.00));
        newVariant3.setDefaultVariant(true);
        VariantDTO variantDTO3 = variantMapper.toDTO(newVariant3);

        apiRequestUtils.postAndExpect200("/api/cms/variants/add", variantDTO1);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", variantDTO2);
        apiRequestUtils.postAndExpect200("/api/cms/variants/add", variantDTO3);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 9, VariantDTO.class);

        VariantDTO persistedVariant1 = variants.getFirst();
        VariantDTO persistedVariant2 = variants.get(1);
        VariantDTO persistedVariant3 = variants.get(2);
        assertEquals(3, variants.size());
        assertFalse(persistedVariant1.defaultVariant());
        assertEquals(Money.of(6.00), persistedVariant2.price());
        assertEquals("Wariat3", persistedVariant3.name().defaultTranslation());
        assertEquals(3, persistedVariant3.displayOrder());
        assertTrue(persistedVariant3.defaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateDefaultFieldInExisting() throws Exception {
        Variant existingVariant = getVariant(4L);
        existingVariant.setDefaultVariant(true);
        VariantDTO variantDTO = variantMapper.toDTO(existingVariant);

        apiRequestUtils.patchAndExpect200("/api/cms/variants/update", variantDTO);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, VariantDTO.class);
        assertEquals(3, variants.size());

        VariantDTO variant1 = variants.getFirst();
        VariantDTO variant2 = variants.get(1);
        VariantDTO variant3 = variants.get(2);
        assertFalse(variant1.defaultVariant());
        assertTrue(variant2.defaultVariant());
        assertFalse(variant3.defaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSwitchDefaultToNextVariant() throws Exception {
        Variant existingVariant = getVariant(3L);
        existingVariant.setDefaultVariant(false);
        VariantDTO variantDTO = variantMapper.toDTO(existingVariant);

        apiRequestUtils.patchAndExpect200("/api/cms/variants/update", variantDTO);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, VariantDTO.class);
        assertEquals(3, variants.size());

        VariantDTO variant1 = variants.getFirst();
        VariantDTO variant2 = variants.get(1);
        VariantDTO variant3 = variants.get(2);
        assertFalse(variant1.defaultVariant());
        assertFalse(variant2.defaultVariant());
        assertFalse(variant3.defaultVariant());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotPersistWithInvalidName() throws Exception {
        Variant newVariant = createVariant(4L, "", Money.of(12.50));
        VariantDTO variantDTO = variantMapper.toDTO(newVariant);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", variantDTO);
        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));

        newVariant.setName(null);
        variantDTO = variantMapper.toDTO(newVariant);
        errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", variantDTO);
        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotPersistWithInvalidPrice() throws Exception {
        Variant newVariant = createVariant(12L, "Wariat", Money.of(-44.00));
        VariantDTO variantDTO = variantMapper.toDTO(newVariant);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/variants/add", variantDTO);
        assertEquals(1, errors.size());
        assertEquals("Cena musi być równa lub większa od 0.00", errors.get("price"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowForbiddenToAddVariant() throws Exception {
        Variant variant = createVariant(12L, "Not hehe", Money.of(5.50));
        VariantDTO variantDTO = variantMapper.toDTO(variant);
        apiRequestUtils.postAndExpect("/api/cms/variants/add", variantDTO, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccess() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/variants/show", 4);
        apiRequestUtils.postAndExpectForbidden("/api/cms/variants/item", 14);
        Variant variant = createVariant(12L, "Not hehe", Money.of(5.50));
        VariantDTO variantDTO = variantMapper.toDTO(variant);
        apiRequestUtils.postAndExpectForbidden("/api/cms/variants/add", variantDTO);
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrders() throws Exception {
        List<VariantDTO> initialVariants = getAllByMenuItemId(21L);
        List<Variant> variants = initialVariants.stream().map(variantMapper::toVariant).toList();

        assertEquals(1, variants.getFirst().getDisplayOrder());
        assertEquals("Mała", variants.getFirst().getName().getDefaultTranslation());

        assertEquals(2, variants.get(1).getDisplayOrder());
        assertEquals("Średnia", variants.get(1).getName().getDefaultTranslation());

        assertEquals(3, variants.get(2).getDisplayOrder());
        assertEquals("Duża", variants.get(2).getName().getDefaultTranslation());

        variants.getFirst().setDisplayOrder(3);
        variants.get(2).setDisplayOrder(1);

        List<VariantDTO> variantDTOs = variants.stream().map(variantMapper::toDTO).toList();
        List<VariantDTO> updatedVariantDTOs =
                apiRequestUtils.patchAndGetList(
                        "/api/cms/variants/display-orders", variantDTOs, VariantDTO.class);

        assertEquals("Duża", updatedVariantDTOs.getFirst().name().defaultTranslation());
        assertEquals(1, updatedVariantDTOs.getFirst().displayOrder());

        assertEquals("Mała", updatedVariantDTOs.get(2).name().defaultTranslation());
        assertEquals(3, updatedVariantDTOs.get(2).displayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldRemoveVariant() throws Exception {
        VariantDTO variant =
                apiRequestUtils.postObjectExpect200("/api/cms/variants/show", 4, VariantDTO.class);
        assertEquals("Średnia", variant.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/variants/delete", variant);

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
        VariantDTO variant =
                apiRequestUtils.postObjectExpect200("/api/cms/variants/show", 4, VariantDTO.class);
        assertEquals("Średnia", variant.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/variants/delete", variant);

        List<VariantDTO> variants =
                apiRequestUtils.postAndGetList(
                        "/api/cms/variants/item", 21, VariantDTO.class);
        assertEquals(2, variants.size());
        assertEquals("Duża", variants.get(1).name().defaultTranslation());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveFirst() throws Exception {
        VariantDTO variant = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", 3, VariantDTO.class);
        assertEquals("Mała", variant.name().defaultTranslation());

        List<VariantDTO> variants =
                apiRequestUtils.deleteAndGetList("/api/cms/variants/delete", variant, VariantDTO.class);

        assertEquals(2, variants.size());

        assertEquals("Średnia", variants.getFirst().name().defaultTranslation());
        assertEquals(1, variants.getFirst().displayOrder());

        assertEquals("Duża", variants.get(1).name().defaultTranslation());
        assertEquals(2, variants.get(1).displayOrder());
    }

    private Variant createVariant(Long menuItemId,
                                  String name,
                                  BigDecimal price) {
        Variant variant = new Variant();
        variant.setMenuItem(menuItemRepository.findById(menuItemId).orElseThrow());
        variant.setName(getDefaultTranslation(name));
        variant.setPrice(price);
        variant.setAvailable(true);
        return variant;
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

    private Variant getVariant(Long id) throws Exception {
        VariantDTO existingVariant = apiRequestUtils.postObjectExpect200(
                "/api/cms/variants/show", id, VariantDTO.class);
        return variantMapper.toVariant(existingVariant);
    }

    private List<VariantDTO> getAllByMenuItemId(Long menuItemId) throws Exception {
        return apiRequestUtils.postAndGetList(
                "/api/cms/variants/item", menuItemId, VariantDTO.class);
    }

}