package com.hackybear.hungry_scan_core.integration.cache;

import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheDataIsolationTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Order(2)
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void getAllCategories_1stUser_FirstCall() throws Exception {
        fetchAndExpect(9);
    }

    @Test
    @Order(3)
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void getAllCategories_2ndUser_Expect0() throws Exception {
        fetchAndExpect(0);
    }

    @Test
    @Order(4)
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void saveCategory_2ndUser() throws Exception {
        apiRequestUtils.postAndExpect200("/api/cms/categories/add", createCategoryFormDTO());
    }

    @Test
    @Order(5)
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void getAllCategories_1stUser_SecondCall() throws Exception {
        fetchAndExpect(9);
    }

    @Test
    @Order(6)
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void getAllCategories_2ndUser_Expect1() throws Exception {
        fetchAndExpect(1);
    }

    @Test
    @Order(7)
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void saveCategory_1stUser() throws Exception {
        apiRequestUtils.postAndExpect200("/api/cms/categories/add", createCategoryFormDTO());
    }

    @Test
    @Order(8)
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void getAllCategories_2ndUser_Expect1_SecondCall() throws Exception {
        fetchAndExpect(1);
    }

    @Test
    @Order(9)
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void getAllCategories_1stUser_ThirdCall() throws Exception {
        fetchAndExpect(10);
    }

    private CategoryFormDTO createCategoryFormDTO() {
        TranslatableDTO translatableDTO = new TranslatableDTO(null, "Food", null, null, null, null, null);
        return new CategoryFormDTO(null, translatableDTO, true, null);
    }

    private void fetchAndExpect(int expectedSize) throws Exception {
        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        assertEquals(expectedSize, categories.size());
    }

}
