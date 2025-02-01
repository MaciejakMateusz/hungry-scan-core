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
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    public void shouldAuthorizeForAdminPanelModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/admin");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "MANAGER", "CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAuthorizeForAdminPanelModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/admin");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    public void shouldAuthorizeForAppModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/app");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "CUSTOMER", "CUSTOMER_READONLY"})
    public void shouldNotAuthorizeForApp() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/app");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "fresh@user.it")
    public void shouldRedirectToCreateRestaurant() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.fetchAndExpect("/api/auth/app", status().isFound());
        assertEquals(302, response.getStatus());
        assertEquals("{\"redirectUrl\":\"/create-restaurant\"}", response.getContentAsString());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldRedirectToDashboardModule() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.fetchAndExpect("/api/auth/create-restaurant", status().isFound());
        assertEquals(302, response.getStatus());
        assertEquals("{\"redirectUrl\":\"/app\"}", response.getContentAsString());
    }

    @Test
    public void shouldAllowToActivation() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/activation");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToActivation() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/activation");
    }

    @Test
    public void shouldAllowToActivationAll() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/activation/?resend=true");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToActivationAll() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/activation/?target=admin@example.com");
    }

    @Test
    public void shouldAllowToActivationError() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/activation-error");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToActivationError() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/activation-error");
    }

    @Test
    public void shouldAllowToAccountActivated() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/account-activated");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToAccountActivated() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/account-activated");
    }

    @Test
    public void shouldAllowToSignIn() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/sign-in");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToSignIn() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/sign-in");
    }

    @Test
    public void shouldAllowToSignUp() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/sign-up");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToSignUp() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/sign-up");
    }

    @Test
    public void shouldAllowToPasswordRecovery() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/password-recovery");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToPasswordRecovery() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/password-recovery");
    }

    @Test
    public void shouldAllowToNewPassword() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/new-password");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToNewPassword() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/new-password");
    }

    @Test
    public void shouldAllowToRecoverySent() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/recovery-sent");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToRecoverySent() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/recovery-sent");
    }

    @Test
    public void shouldAllowToRecoveryConfirmation() throws Exception {
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/auth/recovery-confirmation");
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    public void shouldNotAllowAuthorizedToRecoveryConfirmation() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/recovery-confirmation");
    }
}