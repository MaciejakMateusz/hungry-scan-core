package pl.rarytas.rarytas_restaurantside.controller.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(1)
    void shouldGetAllCategories() throws Exception {
        List<Category> categories =
                apiRequestUtils.fetchObjects(
                        "/api/cms/categories", Category.class);

        assertEquals(8, categories.size());
        assertEquals("Przystawki", categories.get(0).getName());
        assertEquals(5, categories.get(3).getMenuItems().size());
        assertEquals("Napoje", categories.get(7).getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToCategories() throws Exception {
        mockMvc.perform(get("/api/cms/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(3)
    void shouldShowCategoryById() throws Exception {
        Category category = apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 4, Category.class);
        assertEquals("Zupy", category.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToShowUser() throws Exception {
        Integer id = 4;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/cms/categories/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(5)
    void shouldNotShowCategoryById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 55, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(6)
    void shouldGetNewCategoryObject() throws Exception {
        Object category = apiRequestUtils.fetchObject("/api/cms/categories/add", Category.class);
        assertInstanceOf(Category.class, category);
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(7)
    void shouldNotAllowUnauthorizedAccessToNewCategoryObject() throws Exception {
        mockMvc.perform(get("/api/cms/categories/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(8)
    void shouldAddNewCategory() throws Exception {
        Category category = createCategory();

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        Category persistedCategory =
                apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals("Food", persistedCategory.getName());
        assertEquals("Good foot.", persistedCategory.getDescription());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(9)
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        Category category = createCategory();
        apiRequestUtils.postObject("/api/cms/categories/add", category, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(10)
    void shouldNotAddWithIncorrectName() throws Exception {
        Category category = createCategory();
        category.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", category);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(10)
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
        assertEquals("Długość musi wynosić od 0 do 300", errors.get("description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(11)
    void shouldUpdateCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 9, Category.class);
        existingCategory.setName("Foot");

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals("Foot", updatedCategory.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(12)
    void shouldNotAllowUnauthorizedAccessToUpdateCategory() throws Exception {
        Category category = new Category();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/cms/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(13)
    void shouldNotUpdateIncorrectCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 9, Category.class);
        existingCategory.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", existingCategory);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(14)
    void shouldRemoveCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.getObjectExpect200("/api/cms/categories/show", 9, Category.class);

        apiRequestUtils.postAndExpect200("/api/cms/categories/remove", existingCategory);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 9, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 9 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(15)
    void shouldNotAllowUnauthorizedAccessToRemoveCategory() throws Exception {
        Category category = new Category();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/cms/categories/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isForbidden());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Food");
        category.setDescription("Good foot.");
        return category;
    }
}