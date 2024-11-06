package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import jakarta.validation.ConstraintViolationException;
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
        List<CategoryDTO> categories = categoryService.findAll();
        assertEquals(9, categories.size());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        CategoryFormDTO category = categoryService.findById(1L);
        assertEquals("Przystawki", category.name().defaultTranslation());
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
        assertEquals("Pizza", allCategories.get(4).name().defaultTranslation());
        assertEquals("Makarony", allCategories.get(1).name().defaultTranslation());
        assertEquals("Pastas", allCategories.get(1).name().translationEn());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldInsertNew() throws Exception {
        Category newCategory = createCategory();
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(newCategory);

        categoryService.save(categoryFormDTO);

        CategoryFormDTO persistedCategory = categoryService.findById(10L);
        assertEquals("Tajskie", persistedCategory.name().defaultTranslation());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertNewWithBlankName() {
        Category category = new Category();

        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("");
        category.setName(translatable);
        CategoryFormDTO categoryBlank = categoryMapper.toFormDTO(category);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(categoryBlank));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertNewWithNullName() {
        Category category = new Category();

        category.setName(null);
        CategoryFormDTO categoryNull = categoryMapper.toFormDTO(category);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(categoryNull));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldUpdate() throws Exception {
        Category existingCategory = categoryRepository.findById(7L).orElseThrow();
        existingCategory.setName(getTranslationPl());
        CategoryFormDTO categoryFormDTO = categoryMapper.toFormDTO(existingCategory);

        categoryService.update(categoryFormDTO);
        CategoryFormDTO updatedCategory = categoryService.findById(7L);
        assertEquals("Testowe jedzenie", updatedCategory.name().defaultTranslation());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldDelete() throws LocalizedException, AuthenticationException {
        categoryService.delete(7L);
        assertThrows(LocalizedException.class, () -> categoryService.findById(7L));
    }

    private List<CategoryDTO> getCategories() throws LocalizedException, AuthenticationException {
        return categoryService.findAll();
    }

    private Category createCategory() {
        Category category = new Category();
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("Tajskie");
        category.setName(translatable);
        category.setDisplayOrder(10);
        return category;
    }

    private Translatable getTranslationPl() {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("Testowe jedzenie");
        return translatable;
    }
}