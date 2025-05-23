package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
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

import javax.naming.AuthenticationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceImpTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserService userService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @Transactional
    void shouldFindAll() throws LocalizedException, AuthenticationException {
        Long activeMenuId = userService.getActiveMenuId();
        List<CategoryDTO> categories = categoryService.findAll(activeMenuId);
        assertEquals(9, categories.size());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        CategoryFormDTO category = categoryService.findById(1L);
        assertEquals("Przystawki", category.name().pl());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> categoryService.findById(321L));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @Transactional
    public void shouldReturnAll() throws LocalizedException, AuthenticationException {
        List<CategoryDTO> allCategories = getCategories();
        assertEquals(9, allCategories.size());
        assertEquals("Pizza", allCategories.get(4).name().pl());
        assertEquals("Makarony", allCategories.get(1).name().pl());
        assertEquals("Pastas", allCategories.get(1).name().en());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldInsertNew() throws Exception {
        Category newCategory = createCategory();
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(newCategory);

        Long activeMenuId = userService.getActiveMenuId();
        categoryService.save(categoryFormDTO, activeMenuId);

        CategoryFormDTO persistedCategory = categoryService.findById(10L);
        assertEquals("Tajskie", persistedCategory.name().pl());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertNewWithBlankName() throws LocalizedException {
        Category category = new Category();

        Translatable translatable = new Translatable();
        translatable.setPl("");
        category.setName(translatable);
        CategoryFormDTO categoryBlank = categoryMapper.toFormDTO(category);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(categoryBlank, activeMenuId));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertNewWithNullName() throws LocalizedException {
        Category category = new Category();

        category.setName(null);
        CategoryFormDTO categoryNull = categoryMapper.toFormDTO(category);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(ValidationException.class, () -> categoryService.save(categoryNull, activeMenuId));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldUpdate() throws Exception {
        Category existingCategory = categoryRepository.findById(7L).orElseThrow();
        existingCategory.setName(getTranslationPl());
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        Long activeMenuId = userService.getActiveMenuId();
        categoryService.update(categoryFormDTO, activeMenuId);
        CategoryFormDTO updatedCategory = categoryService.findById(7L);
        assertEquals("Testowe jedzenie", updatedCategory.name().pl());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldDelete() throws LocalizedException, AuthenticationException {
        Long activeMenuId = userService.getActiveMenuId();
        categoryService.delete(7L, activeMenuId);
        assertThrows(LocalizedException.class, () -> categoryService.findById(7L));
    }

    private List<CategoryDTO> getCategories() throws LocalizedException, AuthenticationException {
        Long activeMenuId = userService.getActiveMenuId();
        return categoryService.findAll(activeMenuId);
    }

    private Category createCategory() {
        Category category = new Category();
        Translatable translatable = new Translatable();
        translatable.setPl("Tajskie");
        category.setName(translatable);
        category.setDisplayOrder(10);
        return category;
    }

    private Translatable getTranslationPl() {
        Translatable translatable = new Translatable();
        translatable.setPl("Testowe jedzenie");
        return translatable;
    }
}