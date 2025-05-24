package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private CategoryMapper categoryMapper;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MenuRepository menuRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    @Order(2)
    void shouldGetAllCategories() throws Exception {
        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);

        List<MenuItemSimpleDTO> menuItems = categories.get(3).menuItems();
        assertEquals(9, categories.size());
        assertEquals("Przystawki", categories.getFirst().name().pl());
        assertEquals("Napoje", categories.get(7).name().pl());
        assertEquals(5, menuItems.size());
        assertEquals(4, menuItems.getFirst().category().id());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    @Order(3)
    void shouldGetAllDisplayOrders() throws Exception {
        List<Integer> displayOrders =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/display-orders", Integer.class);

        assertEquals(9, displayOrders.size());
        assertEquals(1, displayOrders.getFirst());
        assertEquals(9, displayOrders.get(8));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    @Transactional
    @Rollback
    @Order(4)
    void shouldUpdateDisplayOrders() throws Exception {
        List<CategoryDTO> categoryDTOs =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        List<Category> categories = categoryDTOs.stream().map(categoryMapper::toCategory).toList();

        assertEquals(1, categories.getFirst().getDisplayOrder());
        assertEquals("Przystawki", categories.getFirst().getName().getPl());

        assertEquals(2, categories.get(1).getDisplayOrder());

        assertEquals(3, categories.get(2).getDisplayOrder());
        assertEquals("Sałatki", categories.get(2).getName().getPl());

        assertEquals(4, categories.get(3).getDisplayOrder());
        assertEquals(5, categories.get(4).getDisplayOrder());

        assertEquals(6, categories.get(5).getDisplayOrder());
        assertEquals("Wegetariańskie", categories.get(5).getName().getPl());

        assertEquals(7, categories.get(6).getDisplayOrder());

        assertEquals(8, categories.get(7).getDisplayOrder());

        assertEquals(9, categories.get(8).getDisplayOrder());
        assertEquals("Pusta", categories.get(8).getName().getPl());

        categories.getFirst().setDisplayOrder(9);
        categories.get(8).setDisplayOrder(1);

        categories.get(5).setDisplayOrder(3);
        categories.get(2).setDisplayOrder(6);

        List<CategoryFormDTO> categoryFormDTOs = categories.stream().map(categoryMapper::toFormDTO).toList();
        apiRequestUtils.patchAndExpect200("/api/cms/categories/display-orders", categoryFormDTOs);
        entityManager.clear();

        List<CategoryDTO> updatedCategoryDTOs =
                apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class)
                        .stream()
                        .sorted()
                        .toList();

        assertEquals("Pusta", updatedCategoryDTOs.getFirst().name().pl());
        assertEquals(1, updatedCategoryDTOs.getFirst().displayOrder());

        assertEquals("Przystawki", updatedCategoryDTOs.get(8).name().pl());
        assertEquals(9, updatedCategoryDTOs.get(8).displayOrder());

        assertEquals("Sałatki", updatedCategoryDTOs.get(5).name().pl());
        assertEquals(6, updatedCategoryDTOs.get(5).displayOrder());

        assertEquals("Wegetariańskie", updatedCategoryDTOs.get(2).name().pl());
        assertEquals(3, updatedCategoryDTOs.get(2).displayOrder());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"}, username = "admin@example.com")
    @Order(5)
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
    @Order(6)
    void shouldGetAllAvailable() throws Exception {
        List<CategoryCustomerDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories/available", CategoryCustomerDTO.class);
        assertEquals(8, categories.size());
        assertEquals(4, categories.getFirst().menuItems().size());
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
        assertEquals("Zupy", category.name().pl());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShow() throws Exception {
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
        CategoryFormDTO category = createCategoryFormDTO("Food");
        Optional<Integer> maxDisplayOrder = categoryRepository.findMaxDisplayOrderByMenuId(1L);
        assertEquals(9, maxDisplayOrder.orElse(0));

        apiRequestUtils.postAndExpect200("/api/cms/categories/add", category);

        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        CategoryDTO persistedCategory = categories.getLast();
        assertEquals("Food", persistedCategory.name().pl());
        assertNull(persistedCategory.name().en());
        assertEquals(10, persistedCategory.displayOrder());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToAddCategory() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO("Food");
        apiRequestUtils.postAndExpect("/api/cms/categories/add", category, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddCategory() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO("Food");
        apiRequestUtils.postAndExpectForbidden("/api/cms/categories/add", category);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotAddWithIncorrectName() throws Exception {
        CategoryFormDTO category = createCategoryFormDTO("");

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
        apiRequestUtils.patchAndExpectForbidden("/api/cms/categories/update", new Category());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateCategory() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/categories/update", new Category());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotUpdateIncorrectCategory() throws Exception {
        Category existingCategory = getCategory(6L);
        existingCategory.setName(getDefaultTranslation(""));
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/categories/update", categoryFormDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveCategory() throws Exception {
        Category existingCategory = getCategory(7L);
        assertEquals("Dla dzieci", existingCategory.getName().getPl());

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
        assertEquals("Dla dzieci", existingCategory.name().pl());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 7);

        List<CategoryDTO> categories = apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        assertEquals(8, categories.size());
        assertEquals("Napoje", categories.get(6).name().pl());
        assertEquals(5, categories.get(4).displayOrder());
        assertEquals(6, categories.get(5).displayOrder());
        assertEquals(7, categories.get(6).displayOrder());
        assertEquals(8, categories.get(7).displayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveLast() throws Exception {
        CategoryFormDTO existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 9, CategoryFormDTO.class);
        assertEquals("Pusta", existingCategory.name().pl());

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 9);

        List<CategoryDTO> categories = apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);
        assertEquals(8, categories.size());
        assertEquals("Napoje", categories.get(7).name().pl());
        assertEquals(1, categories.getFirst().displayOrder());
        assertEquals(2, categories.get(1).displayOrder());
        assertEquals(3, categories.get(2).displayOrder());
        assertEquals(4, categories.get(3).displayOrder());
        assertEquals(5, categories.get(4).displayOrder());
        assertEquals(6, categories.get(5).displayOrder());
        assertEquals(7, categories.get(6).displayOrder());
        assertEquals(8, categories.get(7).displayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveFirst() throws Exception {
        CategoryFormDTO existingCategory =
                apiRequestUtils.postObjectExpect200("/api/cms/categories/show", 1, CategoryFormDTO.class);
        assertEquals("Przystawki", existingCategory.name().pl());

        Category category = categoryMapper.toCategory(existingCategory);
        category.setMenu(menuRepository.findById(1L).orElseThrow());
        category.setMenuItems(new HashSet<>());
        categoryRepository.saveAndFlush(category);

        apiRequestUtils.deleteAndExpect200("/api/cms/categories/delete", 1);
        List<CategoryDTO> categories = apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);

        assertEquals(8, categories.size());

        assertEquals("Makarony", categories.getFirst().name().pl());
        assertEquals(1, categories.getFirst().displayOrder());

        assertEquals(2, categories.get(1).displayOrder());

        assertEquals("Zupy", categories.get(2).name().pl());
        assertEquals(3, categories.get(2).displayOrder());

        assertEquals(4, categories.get(3).displayOrder());
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

    private CategoryFormDTO createCategoryFormDTO(String name) {
        TranslatableDTO translatableDTO = new TranslatableDTO(null, name, null, null, null, null, null);
        return new CategoryFormDTO(null, translatableDTO, true, null);
    }

    private Translatable getDefaultTranslation(String value) {
        Translatable translatable = new Translatable();
        translatable.setPl(value);
        return translatable;
    }

    private Category getCategory(Long id) {
        Category category = categoryRepository.findByIdFetchMenuItems(id).orElseThrow();
        entityManager.detach(category);
        return category;
    }
}