package com.hackybear.hungry_scan_core.integration.cache;

import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CachingTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(CATEGORIES_ALL)).clear();
        Objects.requireNonNull(cacheManager.getCache(CATEGORY_ID)).clear();
    }

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {STAFF}, username = "matimemek@test.com")
    @Order(2)
    void getAllCategoriesTest() throws Exception {
        Cache categoriesCache = cacheManager.getCache(CATEGORIES_ALL);
        assertNotNull(categoriesCache);
        assertNull(categoriesCache.get(1L));

        apiRequestUtils.fetchAsList("/api/cms/categories", CategoryDTO.class);

        List<?> cached = categoriesCache.get(1L, List.class);
        assertNotNull(cached);
        assertEquals(9, cached.size());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Order(3)
    void showCategoryByIdTest() throws Exception {
        Cache categoryIdCache = cacheManager.getCache(CATEGORY_ID);
        assertNotNull(categoryIdCache);
        assertNull(categoryIdCache.get(4L));

        CategoryFormDTO category =
                apiRequestUtils.postObjectExpect200(
                        "/api/cms/categories/show", 4, CategoryFormDTO.class);

        assertEquals("Zupy", category.name().pl());

        CategoryFormDTO cached = categoryIdCache.get(4L, CategoryFormDTO.class);
        assertNotNull(cached);
        assertEquals("Zupy", cached.name().pl());
    }


}