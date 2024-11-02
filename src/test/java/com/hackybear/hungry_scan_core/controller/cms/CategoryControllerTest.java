package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import jakarta.persistence.EntityManager;
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

    @Autowired
    private MenuService menuService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private EntityManager entityManager;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    void shouldGetAllCategories() throws Exception {
        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);

        List<MenuItemSimpleDTO> menuItems = categories.get(3).menuItems();
        assertEquals(9, categories.size());
        assertEquals("Przystawki", categories.get(0).name().defaultTranslation());
        assertEquals("Napoje", categories.get(7).name().defaultTranslation());
        assertEquals(5, menuItems.size());
        assertEquals(4, menuItems.get(0).categoryId());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    void shouldGetAllDisplayOrders() throws Exception {
        List<Integer> displayOrders =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/display-orders", Integer.class);

        assertEquals(9, displayOrders.size());
        assertEquals(1, displayOrders.get(0));
        assertEquals(9, displayOrders.get(8));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"}, username = "admin@example.com")
    void shouldCount() throws Exception {
        Integer count =
                apiRequestUtils.fetchObject(
                        "/api/cms/categories/count", Integer.class);
        assertEquals(9, count);
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "CUSTOMER_READONLY", "CUSTOMER"}, username = "matimemek@test.com")
    void shouldNotAllowAccessToCount() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/count");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"}, username = "ff3abf8-9b6a@temp.it")
    void shouldGetAllAvailable() throws Exception {
        List<CategoryCustomerDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/available", CategoryCustomerDTO.class);
        assertEquals(8, categories.size());
        assertEquals(4, categories.get(0).menuItems().size());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToGetAllAvailable() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/available");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCount() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/count");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCategories() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories");
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/display-orders");
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/categories/count");
    }

    @Test
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    void shouldShowCategoryById() throws Exception {
        CategoryFormDTO category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 4, CategoryFormDTO.class);
        assertEquals("Zupy", category.name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowUser() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/categories/show", 4);
    }

    @Test
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    void shouldNotShowCategoryById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 55, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNewCategory() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO(null, "Food", 10);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        CategoryDTO persistedCategory = categories.get(categories.size() - 1);
        assertEquals("Food", persistedCategory.name().defaultTranslation());
        assertNull(persistedCategory.name().translationEn());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToAddCategory() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO(null, "Food", 10);
        apiRequestUtils.postAndExpect("/api/cms/categories/add", category, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddCategory() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO(null, "Food", 10);
        apiRequestUtils.postAndExpectForbidden("/api/cms/categories/add", category);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotAddWithIncorrectName() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO(null, "", 10);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", category);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateCategory() throws Exception {
        Category existingCategory = getCategory(6L);
        existingCategory.setName(getDefaultTranslation("Foot"));
        assertEquals(5, existingCategory.getMenuItems().size());
        assertTrue(existingCategory.isAvailable());
        assertEquals(6, existingCategory.getDisplayOrder());
        assertEquals("2024-10-27T11:24:07.783228", existingCategory.getCreated().toString());
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        existingCategory = getCategory(6L);
        assertEquals(5, existingCategory.getMenuItems().size());
        assertTrue(existingCategory.isAvailable());
        assertEquals(6, existingCategory.getDisplayOrder());
        assertEquals("2024-10-27T11:24:07.783228", existingCategory.getCreated().toString());
        assertNotNull(existingCategory.getUpdated());
        assertEquals("admin@example.com", existingCategory.getModifiedBy());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToUpdateCategory() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/categories/add", new Category(), status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateCategory() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/categories/add", new Category());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotUpdateIncorrectCategory() throws Exception {
        Category existingCategory = getCategory(6L);
        existingCategory.setName(getDefaultTranslation(""));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/categories/add", categoryFormDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToBigger() throws Exception {
        Category existingCategory = getCategory(1L);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(3);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        existingCategory.setMenuId(1L);
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals(3, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, CategoryFormDTO.class);
        assertEquals(2, thirdToSecond.displayOrder());

        CategoryFormDTO secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, CategoryFormDTO.class);
        assertEquals(1, secondToFirst.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLower() throws Exception {
        Category existingCategory = getCategory(5L);
        assertEquals(5, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(2);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 5, CategoryFormDTO.class);
        assertEquals(2, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, CategoryFormDTO.class);
        assertEquals(3, secondToThird.displayOrder());

        CategoryFormDTO fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 4, CategoryFormDTO.class);
        assertEquals(5, fourthToFifth.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLastWithTooBigValue() throws Exception {
        Category existingCategory = getCategory(1L);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(15);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals(9, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO ninthToEight =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, CategoryFormDTO.class);
        assertEquals(8, ninthToEight.displayOrder());

        CategoryFormDTO thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, CategoryFormDTO.class);
        assertEquals(2, thirdToSecond.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToFirstWithTooLowValue() throws Exception {
        Category existingCategory = getCategory(3L);
        assertEquals(3, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(-2);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 3, CategoryFormDTO.class);
        assertEquals(1, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals(2, firstToSecond.displayOrder());

        CategoryFormDTO secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, CategoryFormDTO.class);
        assertEquals(3, secondToThird.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromLastToFirst() throws Exception {
        Category existingCategory = getCategory(9L);
        assertEquals(9, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(1);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, CategoryFormDTO.class);
        assertEquals(1, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals(2, firstToSecond.displayOrder());

        CategoryFormDTO eightToNinth =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 8, CategoryFormDTO.class);
        assertEquals(9, eightToNinth.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromFirstToLast() throws Exception {
        Category existingCategory = getCategory(1L);
        assertEquals(1, existingCategory.getDisplayOrder());
        existingCategory.setDisplayOrder(9);
        existingCategory.setName(getDefaultTranslation("Updated category"));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", categoryFormDTO);

        CategoryFormDTO updatedCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals(9, updatedCategory.displayOrder());
        assertEquals("Updated category", updatedCategory.name().defaultTranslation());

        CategoryFormDTO secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 2, CategoryFormDTO.class);
        assertEquals(1, secondToFirst.displayOrder());

        CategoryFormDTO ninthToEight =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, CategoryFormDTO.class);
        assertEquals(8, ninthToEight.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNewWithExistingDisplayOrder() throws Exception {
        CategoryFormDTO categoryFormDTO = createCategoryFormDTO(null, "Food", 2);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", categoryFormDTO);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);

        assertEquals(10, categories.size());
        assertEquals("Food", categories.get(1).name().defaultTranslation());
        assertEquals(2, categories.get(1).displayOrder());
        assertEquals("Przystawki", categories.get(0).name().defaultTranslation());
        assertEquals("Makarony", categories.get(2).name().defaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddFirstAssertDisplayOrder1() throws Exception {
        CategoryFormDTO categoryFormDTO = createCategoryFormDTO(null, "Food", 55);

        variantRepository.deleteAll();
        menuItemRepository.deleteAll();
        categoryRepository.deleteAll();

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", categoryFormDTO);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        assertEquals(1, categories.size());
        CategoryDTO categoryDTO = categories.get(0);
        assertEquals("Food", categoryDTO.name().defaultTranslation());
        assertEquals(1, categoryDTO.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldHandleBoundaryDisplayOrders() throws Exception {
        CategoryFormDTO food = createCategoryFormDTO(null, "Food", Integer.MIN_VALUE);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        assertEquals(9, categories.size());

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", food);

        categories =
                apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        Menu updatedMenu = menuService.findById(1L);

        assertEquals(10, updatedMenu.getCategories().size());
        assertEquals(10, categories.size());
        CategoryDTO foodDTO = categories.get(0);
        assertEquals("Food", foodDTO.name().defaultTranslation());
        assertEquals(1, foodDTO.displayOrder());
        assertEquals("Przystawki", categories.get(1).name().defaultTranslation());
        assertEquals("Starters", categories.get(1).name().translationEn());
        assertEquals(2, categories.get(1).displayOrder());

        food = createCategoryFormDTO(foodDTO.id(), "Food", Integer.MAX_VALUE);
        apiRequestUtils.patchAndExpect200("/api/cms/categories/update", food);

        categories = apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        foodDTO = categories.get(categories.size() - 1);

        assertEquals("Food", foodDTO.name().defaultTranslation());
        assertEquals(10, foodDTO.displayOrder());
        assertEquals("Przystawki", categories.get(0).name().defaultTranslation());
        assertEquals(1, categories.get(0).displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldHandleMiddleReordering() throws Exception {
        CategoryFormDTO food = createCategoryFormDTO(null, "Food", 3);

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", food);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        assertEquals(10, categories.size());
        CategoryDTO foodDTO = categories.get(2);
        assertEquals("Food", foodDTO.name().defaultTranslation());
        assertEquals(3, foodDTO.displayOrder());
        assertEquals("Sałatki", categories.get(3).name().defaultTranslation());
        assertEquals(4, categories.get(3).displayOrder());

        food = createCategoryFormDTO(foodDTO.id(), "Food", 5);
        apiRequestUtils.postAndExpect200("/api/cms/categories/add", food);

        categories = apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        foodDTO = categories.get(4);
        assertEquals("Food", foodDTO.name().defaultTranslation());
        assertEquals(5, foodDTO.displayOrder());
        assertEquals("Pizza", categories.get(5).name().defaultTranslation());
        assertEquals(6, categories.get(5).displayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveCategory() throws Exception {
        Category existingCategory = getCategory(7L);
        assertEquals("Dla dzieci", existingCategory.getName().getDefaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 7);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/categories/show", 7, status().isBadRequest());
        assertEquals("Kategoria z podanym ID = 7 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrderAfterRemoval() throws Exception {
        CategoryFormDTO existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 7, CategoryFormDTO.class);
        assertEquals("Dla dzieci", existingCategory.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 7);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        assertEquals(8, categories.size());
        assertEquals("Napoje", categories.get(6).name().defaultTranslation());
        assertEquals(5, categories.get(4).displayOrder());
        assertEquals(6, categories.get(5).displayOrder());
        assertEquals(7, categories.get(6).displayOrder());
        assertEquals(8, categories.get(7).displayOrder());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowAccessToRemoveCategory() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/categories/delete", 5, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRemoveCategory() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/categories/delete", 5, status().isForbidden());
    }

    private CategoryFormDTO createCategoryFormDTO(Long id, String name, Integer displayOrder) {
        TranslatableDTO translatableDTO = new TranslatableDTO(null, name, null);
        return new CategoryFormDTO(id, translatableDTO, true, displayOrder);
    }

    private Translatable getDefaultTranslation(String value) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(value);
        return translatable;
    }

    private Category getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        entityManager.detach(category);
        return category;
    }
}