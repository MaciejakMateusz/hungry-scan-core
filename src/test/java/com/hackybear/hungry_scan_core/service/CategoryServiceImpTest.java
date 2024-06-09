package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

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

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    void shouldFindAll() {
        List<Category> categories = categoryService.findAll();
        assertEquals(9, categories.size());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        Category category = categoryService.findById(1);
        assertEquals("Przystawki", category.getName().getDefaultTranslation());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> categoryService.findById(321));
    }

    @Test
    public void shouldReturnAll() {
        List<Category> allCategories = getCategories();
        assertEquals(9, allCategories.size());
        assertEquals("Pizza", allCategories.get(4).getName().getDefaultTranslation());
        assertEquals("Makarony", allCategories.get(1).getName().getDefaultTranslation());
        assertEquals("Pastas", allCategories.get(1).getName().getTranslationEn());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldInsertNew() throws Exception {
        Category newCategory = createCategory();
        categoryService.save(newCategory);
        Category category = categoryService.findById(newCategory.getId());
        assertEquals("Tajskie", category.getName().getDefaultTranslation());
    }

    @Test
    public void shouldNotInsertNew() {
        Category category = new Category();

        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("");
        category.setName(translatable);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));

        category.setName(null);
        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdate() throws Exception {
        Category existingCategory = categoryService.findById(7);
        existingCategory.setName(getTranslationPl());
        categoryService.save(existingCategory);
        Category updatedCategory = categoryService.findById(7);
        assertEquals("Testowe jedzenie", updatedCategory.getName().getDefaultTranslation());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDelete() throws LocalizedException {
        categoryService.delete(7);
        assertThrows(LocalizedException.class, () -> categoryService.findById(7));
    }

    @Test
    public void shouldNotDelete() {
        LocalizedException exception = assertThrows(LocalizedException.class, () -> categoryService.delete(1));
        assertEquals("Kategoria posiada w sobie dania.", exception.getLocalizedMessage());
    }

    private List<Category> getCategories() {
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