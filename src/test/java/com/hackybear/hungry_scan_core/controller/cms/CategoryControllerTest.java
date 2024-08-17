package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
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
class CategoryControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldGetAllCategories() throws Exception {
        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);

        assertEquals(9, categories.size());
        assertEquals("Przystawki", categories.get(0).getName().getDefaultTranslation());
        assertEquals("Napoje", categories.get(7).getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldGetAllDisplayOrders() throws Exception {
        List<Integer> displayOrders =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/display-orders", Integer.class);

        assertEquals(9, displayOrders.size());
        assertEquals(1, displayOrders.get(0));
        assertEquals(9, displayOrders.get(8));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void shouldCount() throws Exception {
        Integer count =
                apiRequestUtils.fetchObject(
                        "/api/cms/categories/count", Integer.class);
        assertEquals(9, count);
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "CUSTOMER_READONLY", "CUSTOMER"})
    void shouldNotAllowAccessToCount() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/count");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldGetAllAvailable() throws Exception {
        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/available", Category.class);
        assertEquals(8, categories.size());
        assertEquals(4, categories.get(0).getMenuItems().size());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToGetAllAvailable() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories/available");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCount() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories/count");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCategories() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories");
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories/display-orders");
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories/count");
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowCategoryById() throws Exception {
        Category category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 4, Category.class);
        assertEquals("Zupy", category.getName().getDefaultTranslation());
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
    void shouldNotAllowAccessToNewCategoryObject() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/add");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCategoryObject() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/categories/add");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewCategory() throws Exception {
        Category category = createCategory();

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);
        Category persistedCategory = categories.get(categories.size() - 1);
        assertEquals("Food", persistedCategory.getName().getDefaultTranslation());
        assertNull(persistedCategory.getName().getTranslationEn());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToAddCategory() throws Exception {
        Category category = createCategory();
        apiRequestUtils.postAndExpect("/api/cms/categories/add", category, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddCategory() throws Exception {
        Category category = createCategory();
        apiRequestUtils.postAndExpect("/api/cms/categories/add", category, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectName() throws Exception {
        Category category = createCategory();
        category.setName(getDefaultTranslation(""));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", category);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldUpdateCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        existingCategory.setName(getDefaultTranslation("Foot"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        assertEquals("Foot", updatedCategory.getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToUpdateCategory() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/categories/add", new Category(), status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateCategory() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/categories/add", new Category(), status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateIncorrectCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 6, Category.class);
        existingCategory.setName(getDefaultTranslation(""));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", existingCategory);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToBigger() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(3);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(3, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, Category.class);
        assertEquals(2, thirdToSecond.getDisplayOrder());

        Category secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, Category.class);
        assertEquals(1, secondToFirst.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLower() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 5, Category.class);
        assertEquals(5, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(2);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 5, Category.class);
        assertEquals(2, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, Category.class);
        assertEquals(3, secondToThird.getDisplayOrder());

        Category fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 4, Category.class);
        assertEquals(5, fourthToFifth.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLastWithTooBigValue() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(15);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(9, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category ninthToEight =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals(8, ninthToEight.getDisplayOrder());

        Category thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, Category.class);
        assertEquals(2, thirdToSecond.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToFirstWithTooLowValue() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, Category.class);
        assertEquals(3, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(-2);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, Category.class);
        assertEquals(1, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(2, firstToSecond.getDisplayOrder());

        Category secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, Category.class);
        assertEquals(3, secondToThird.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromLastToFirst() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals(9, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(1);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals(1, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(2, firstToSecond.getDisplayOrder());

        Category eightToNinth =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 8, Category.class);
        assertEquals(9, eightToNinth.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromFirstToLast() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(9);
        existingCategory.setName(getDefaultTranslation("Updated category"));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", existingCategory);

        Category updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, Category.class);
        assertEquals(9, updatedCategory.getDisplayOrder());
        assertEquals("Updated category", updatedCategory.getName().getDefaultTranslation());

        Category secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, Category.class);
        assertEquals(1, secondToFirst.getDisplayOrder());

        Category ninthToEight =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, Category.class);
        assertEquals(8, ninthToEight.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewWithExistingDisplayOrder() throws Exception {
        Category category = createCategory();
        category.setDisplayOrder(2);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);

        assertEquals(10, categories.size());
        assertEquals("Food", categories.get(1).getName().getDefaultTranslation());
        assertEquals(2, categories.get(1).getDisplayOrder());
        assertEquals("Przystawki", categories.get(0).getName().getDefaultTranslation());
        assertEquals("Makarony", categories.get(2).getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddFirstAssertDisplayOrder1() throws Exception {
        Category category = createCategory();

        variantRepository.deleteAll();
        menuItemRepository.deleteAll();
        categoryRepository.deleteAll();

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);
        assertEquals(1, categories.size());
        Category persistedCategory = categories.get(0);
        assertEquals("Food", persistedCategory.getName().getDefaultTranslation());
        assertEquals(1, persistedCategory.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldHandleBoundaryDisplayOrders() throws Exception {
        Category category = createCategory();
        category.setDisplayOrder(Integer.MIN_VALUE);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<Category> categories =
                apiRequestUtils.fetchAsList("/api/cms/categories", Category.class);
        assertEquals(10, categories.size());
        category = categories.get(0);
        assertEquals("Food", category.getName().getDefaultTranslation());
        assertEquals(1, category.getDisplayOrder());
        assertEquals("Przystawki", categories.get(1).getName().getDefaultTranslation());
        assertEquals("Starters", categories.get(1).getName().getTranslationEn());
        assertEquals(2, categories.get(1).getDisplayOrder());

        category.setDisplayOrder(Integer.MAX_VALUE);
        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        categories = apiRequestUtils.fetchAsList("/api/cms/categories", Category.class);
        category = categories.get(categories.size() - 1);

        assertEquals("Food", category.getName().getDefaultTranslation());
        assertEquals(10, category.getDisplayOrder());
        assertEquals("Przystawki", categories.get(0).getName().getDefaultTranslation());
        assertEquals(1, categories.get(0).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldHandleMiddleReordering() throws Exception {
        Category category = createCategory();
        category.setDisplayOrder(3);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<Category> categories =
                apiRequestUtils.fetchAsList("/api/cms/categories", Category.class);
        assertEquals(10, categories.size());
        category = categories.get(2);
        assertEquals("Food", category.getName().getDefaultTranslation());
        assertEquals(3, category.getDisplayOrder());
        assertEquals("Sałatki", categories.get(3).getName().getDefaultTranslation());
        assertEquals(4, categories.get(3).getDisplayOrder());

        category.setDisplayOrder(5);
        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        categories = apiRequestUtils.fetchAsList("/api/cms/categories", Category.class);
        category = categories.get(4);
        assertEquals("Food", category.getName().getDefaultTranslation());
        assertEquals(5, category.getDisplayOrder());
        assertEquals("Pizza", categories.get(5).getName().getDefaultTranslation());
        assertEquals(6, categories.get(5).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldRemoveCategory() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 7, Category.class);
        assertEquals("Dla dzieci", existingCategory.getName().getDefaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 7);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 7, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 7 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrderAfterRemoval() throws Exception {
        Category existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 7, Category.class);
        assertEquals("Dla dzieci", existingCategory.getName().getDefaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 7);

        List<Category> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", Category.class);
        assertEquals(8, categories.size());
        assertEquals("Napoje", categories.get(6).getName().getDefaultTranslation());

    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowAccessToRemoveCategory() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/categories/delete", 5, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRemoveCategory() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/categories/delete", 5, status().isUnauthorized());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName(getDefaultTranslation("Food"));
        category.setDisplayOrder(10);
        return category;
    }

    private Translatable getDefaultTranslation(String value) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(value);
        return translatable;
    }
}