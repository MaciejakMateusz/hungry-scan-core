package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.USER_RESTAURANT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void clearRestaurantIdCache() {
        Objects.requireNonNull(cacheManager.getCache(USER_RESTAURANT_ID)).clear();
    }

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "MANAGER", "ADMIN"})
    public void shouldAuthorizeForRestaurantModule() throws Exception {
        boolean isAuthorized = apiRequestUtils.fetchObject("/api/auth/restaurant", Boolean.class);
        assertTrue(isAuthorized);
    }

    @Test
    public void shouldNotAllowUnauthorizedForRestaurantModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/restaurant");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAllowCustomerForRestaurantModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/restaurant");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    public void shouldAuthorizeForCmsModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.getResponse("/api/auth/cms");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAuthorizeForCmsModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/cms");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    public void shouldAuthorizeForAdminPanelModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.getResponse("/api/auth/admin");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "MANAGER", "CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAuthorizeForAdminPanelModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    public void shouldAuthorizeForDashboardModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.getResponse("/api/auth/dashboard");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAuthorizeForDashboardModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/dashboard");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "fresh@user.it")
    public void shouldRedirectToCreateRestaurant() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.fetchAndExpect("/api/auth/cms", status().isFound());
        assertEquals(302, response.getStatus());
        assertEquals("{\"redirectUrl\":\"/create-restaurant\"}", response.getContentAsString());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldRedirectToDashboardModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.fetchAndExpect("/api/auth/create-restaurant", status().isFound());
        assertEquals(302, response.getStatus());
        assertEquals("{\"redirectUrl\":\"/dashboard\"}", response.getContentAsString());
    }
}