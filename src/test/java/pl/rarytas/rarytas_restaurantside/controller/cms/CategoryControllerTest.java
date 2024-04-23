package pl.rarytas.rarytas_restaurantside.controller.cms;

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
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldGetAllCategories() throws Exception {
        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);

        assertEquals(8, categories.size());
        assertEquals("Przystawki", categories.get(0).getName());
        assertEquals("Napoje", categories.get(7).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCategories() throws Exception {
        mockMvc.perform(get("/api/cms/categories")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowCategoryById() throws Exception {
        Category category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 4, Category.class);
        assertEquals("Zupy", category.getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowUser() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/categories/show", 4, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotShowCategoryById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 55, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldGetNewCategoryObject() throws Exception {
        Object category = apiRequestUtils.fetchObject("/api/cms/categories/add", Category.class);
        assertInstanceOf(Category.class, category);
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToNewCategoryObject() throws Exception {
        mockMvc.perform(get("/api/cms/categories/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewCategory() throws Exception {
        Category category = createCategory();

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        Category persistedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals("Food", persistedCategory.getName());
        assertEquals("Good foot.", persistedCategory.getDescription());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        Category category = createCategory();
        apiRequestUtils.postAndExpect("/api/cms/categories/add", category, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectName() throws Exception {
        Category category = createCategory();
        category.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", category);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithTooLongDescription() throws Exception {
        Category category = createCategory();
        category.setDescription("""
                Indulge your senses in a culinary journey with our exquisite range of delectable delights, carefully\s
                curated to tantalize your taste buds and satisfy your cravings. From savory appetizers that awaken your\s
                palate to sumptuous main courses that embody culinary perfection, our food category is a symphony of\s
                flavors, textures, and aromas. Immerse yourself in the rich tapestry of global cuisines, where each\s
                dish is a masterpiece crafted with precision and passion.
                Explore the vibrant tapestry of tastes, from the spicy and aromatic profiles of Asian cuisine to the\s
                comforting familiarity of classic Western dishes. Our menu is a celebration of fresh, locally sourced\s
                ingredients that elevate every bite, ensuring a harmonious blend of nutrition and indulgence.\s
                Whether you seek the bold and spicy, the subtle and sophisticated, or the comforting and hearty, our\s
                diverse selection caters to every palate and preference.
                Elevate your dining experience with our artisanal desserts, where sweetness meets innovation. Savor\s
                the velvety textures, decadent flavors, and artistic presentations that transform each dessert into a\s
                work of edible art. From traditional favorites to avant-garde creations, our dessert collection is a\s
                testament to the creativity and skill of our culinary artisans.
                Not just a meal, but a sensorial experience, our food category goes beyond nourishment, offering a\s
                symphony of tastes and aromas that transport you to gastronomic bliss. Imbued with a commitment to\s
                quality and culinary excellence, our offerings are designed to redefine your dining experience, leaving\s
                an indelible mark on your culinary memories. Embrace the extraordinary, savor the exceptional – welcome\s
                to a world where food is an art form, and every bite tells a story.
                """);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", category);

        assertEquals(1, errors.size());
        assertEquals("Długość musi wynosić od 0 do 300.", errors.get("description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldUpdateCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        existingCategory.setName("Foot");

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        assertEquals("Foot", updatedCategory.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUpdateCategory() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/categories/add", new Category(), status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateIncorrectCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        existingCategory.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", existingCategory);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldRemoveCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        assertNotNull(existingCategory);

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 6);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 6, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 6 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToRemoveCategory() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/categories/delete", 5, status().isForbidden());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Food");
        category.setDescription("Good foot.");
        return category;
    }
}