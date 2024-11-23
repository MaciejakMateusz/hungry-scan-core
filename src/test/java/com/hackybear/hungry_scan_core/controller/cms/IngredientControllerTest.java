package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.IngredientMapper;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.utility.Money;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IngredientControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Autowired
    IngredientMapper ingredientMapper;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    void shouldGetAllIngredients() throws Exception {
        Page<IngredientDTO> ingredients = apiRequestUtils.fetchAsPage(
                "/api/cms/ingredients", getPageableParams(), IngredientDTO.class);

        List<IngredientDTO> ingredientList = ingredients.getContent();
        assertEquals(27, ingredientList.size());
        assertEquals("Bazylia", ingredientList.getFirst().name().defaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowUnauthorizedAccessToIngredients() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/ingredients", getPageableParams(), status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldShowIngredientById() throws Exception {
        IngredientDTO ingredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, IngredientDTO.class);
        assertEquals("Mozzarella", ingredient.name().defaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowUnauthorizedAccessToShowIngredient() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/ingredients/show", 7, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowIngredientById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/ingredients/show", 2841, status().isBadRequest());
        assertEquals("Składnik z podanym ID = 2 841 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNewIngredient() throws Exception {
        Ingredient ingredient = createIngredient("Majeranek", Money.of(1.00));
        IngredientSimpleDTO ingredientDTO = ingredientMapper.toSimpleDTO(ingredient);

        apiRequestUtils.postAndExpect200("/api/cms/ingredients/add", ingredientDTO);

        IngredientDTO persistedIngredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 28, IngredientDTO.class);
        assertEquals("Majeranek", persistedIngredient.name().defaultTranslation());
        assertEquals(BigDecimal.valueOf(1.0), persistedIngredient.price());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToAddIngredient() throws Exception {
        Ingredient ingredient = createIngredient("Muchomor", Money.of(350.00));
        IngredientSimpleDTO ingredientDTO = ingredientMapper.toSimpleDTO(ingredient);
        apiRequestUtils.postAndExpect("/api/cms/ingredients/add", ingredientDTO, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        Ingredient ingredient = createIngredient("", Money.of(0.00));
        IngredientSimpleDTO ingredientDTO = ingredientMapper.toSimpleDTO(ingredient);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/ingredients/add", ingredientDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateExistingIngredient() throws Exception {
        IngredientDTO ingredientDTO =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, IngredientDTO.class);
        assertEquals("Mozzarella", ingredientDTO.name().defaultTranslation());

        Ingredient existingIngredient = ingredientMapper.toIngredient(ingredientDTO);
        existingIngredient.setName(getDefaultTranslation("Updated mozzarella"));
        existingIngredient.setPrice(Money.of(4.00));
        IngredientSimpleDTO simpleDTO = ingredientMapper.toSimpleDTO(existingIngredient);

        apiRequestUtils.patchAndExpect200("/api/cms/ingredients/update", simpleDTO);

        IngredientDTO updatedIngredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, IngredientDTO.class);
        assertEquals("Updated mozzarella", updatedIngredient.name().defaultTranslation());
        assertEquals(BigDecimal.valueOf(4.0), updatedIngredient.price());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    @Rollback
    void shouldDeleteIngredient() throws Exception {
        IngredientDTO ingredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, IngredientDTO.class);
        assertEquals("Mozzarella", ingredient.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/ingredients/delete", 6);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/ingredients/show", 6, status().isBadRequest());
        assertEquals("Składnik z podanym ID = 6 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToRemoveMenuItem() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/ingredients/delete", 12, status().isForbidden());
    }

    private Ingredient createIngredient(String name, BigDecimal price) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(getDefaultTranslation(name));
        ingredient.setPrice(price);
        return ingredient;
    }

    private Translatable getDefaultTranslation(String value) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(value);
        return translatable;
    }

    private Map<String, Object> getPageableParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", 40);
        params.put("pageNumber", 0);
        return params;
    }
}