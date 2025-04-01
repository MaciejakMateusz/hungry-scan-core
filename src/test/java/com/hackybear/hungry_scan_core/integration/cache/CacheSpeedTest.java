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
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.CATEGORIES_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheSpeedTest {

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
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    @Order(2)
    void getAllCategoriesTest() throws Exception {
        long firstRequestBegin = System.currentTimeMillis();
        List<CategoryDTO> firstCategoriesCall =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        long firstRequestEnd = System.currentTimeMillis();
        assertEquals(9, firstCategoriesCall.size());
        long firstRequestResult = firstRequestEnd - firstRequestBegin;

        long secondRequestBegin = System.currentTimeMillis();
        List<CategoryDTO> secondCategoriesCall =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        long secondRequestEnd = System.currentTimeMillis();
        assertEquals(9, secondCategoriesCall.size());
        long secondRequestResult = secondRequestEnd - secondRequestBegin;

        String prepMsg = String.format("Cache did not improve execution time. Initial ms: %s, Repeated ms; %s",
                firstRequestResult, secondRequestResult);
        assertTrue(firstRequestResult > secondRequestResult, prepMsg);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Order(3)
    void showCategoryByIdTest() throws Exception {
        long firstRequestBegin = System.currentTimeMillis();
        CategoryFormDTO firstCategoryCall = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 4, CategoryFormDTO.class);
        long firstRequestEnd = System.currentTimeMillis();
        assertEquals("Zupy", firstCategoryCall.name().defaultTranslation());
        long firstRequestResult = firstRequestEnd - firstRequestBegin;

        long secondRequestBegin = System.currentTimeMillis();
        CategoryFormDTO secondCategoryCall = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 4, CategoryFormDTO.class);
        long secondRequestEnd = System.currentTimeMillis();
        assertEquals("Zupy", secondCategoryCall.name().defaultTranslation());
        long secondRequestResult = secondRequestEnd - secondRequestBegin;

        String prepMsg = String.format("Cache did not improve execution time. Initial ms: %s, Repeated ms; %s",
                firstRequestResult, secondRequestResult);
        assertTrue(firstRequestResult > secondRequestResult, prepMsg);
    }

}