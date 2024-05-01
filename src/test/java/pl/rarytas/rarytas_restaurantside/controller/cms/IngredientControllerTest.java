package pl.rarytas.rarytas_restaurantside.controller.cms;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Ingredient;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldGetAllIngredients() throws Exception {
        List<Ingredient> ingredients =
                apiRequestUtils.fetchAsList(
                        "/api/cms/ingredients", Ingredient.class);

        assertEquals(26, ingredients.size());
        assertEquals("Pomidory", ingredients.get(0).getName());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowUnauthorizedAccessToIngredients() throws Exception {
        mockMvc.perform(get("/api/cms/ingredients")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldShowIngredientById() throws Exception {
        Ingredient ingredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, Ingredient.class);
        assertEquals("Mozzarella", ingredient.getName());
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
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldGetNewIngredientObject() throws Exception {
        Object ingredient = apiRequestUtils.fetchObject("/api/cms/ingredients/add", Ingredient.class);
        assertInstanceOf(Ingredient.class, ingredient);
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToIngredientObject() throws Exception {
        mockMvc.perform(get("/api/cms/ingredients/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewIngredient() throws Exception {
        Ingredient ingredient = createIngredient("Majeranek", Money.of(1.00));

        apiRequestUtils.postAndExpect200("/api/cms/ingredients/add", ingredient);

        Ingredient persistedIngredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 27, Ingredient.class);
        assertEquals("Majeranek", persistedIngredient.getName());
        assertEquals(Money.of(1.00), persistedIngredient.getPrice());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToAddIngredient() throws Exception {
        Ingredient ingredient = createIngredient("Muchomor", Money.of(350.00));
        apiRequestUtils.postAndExpect("/api/cms/ingredients/add", ingredient, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        Ingredient ingredient = createIngredient("", Money.of(0.00));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/ingredients/add", ingredient);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingIngredient() throws Exception {
        Ingredient existingIngredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, Ingredient.class);
        assertEquals("Mozzarella", existingIngredient.getName());

        existingIngredient.setName("Updated mozzarella");
        existingIngredient.setPrice(Money.of(4.00));

        apiRequestUtils.postAndExpect200("/api/cms/ingredients/add", existingIngredient);

        Ingredient updatedIngredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, Ingredient.class);
        assertEquals("Updated mozzarella", updatedIngredient.getName());
        assertEquals(Money.of(4.00), updatedIngredient.getPrice());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    @Rollback
    void shouldDeleteIngredient() throws Exception {
        Ingredient ingredient =
                apiRequestUtils.postObjectExpect200("/api/cms/ingredients/show", 6, Ingredient.class);
        assertEquals("Mozzarella", ingredient.getName());

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
        ingredient.setName(name);
        ingredient.setPrice(price);
        return ingredient;
    }
}