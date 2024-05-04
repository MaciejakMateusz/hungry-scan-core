package pl.rarytas.hungry_scan_core.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.hungry_scan_core.test_utils.ApiRequestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "MANAGER", "ADMIN"})
    public void shouldAuthorizeForRestaurantModule() throws Exception {
        boolean isAuthorized = apiRequestUtils.fetchObject("/api/auth/restaurant", Boolean.class);
        assertTrue(isAuthorized);
    }

    @Test
    public void shouldNotAuthorizeForRestaurantModule() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/auth/restaurant");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    public void shouldAuthorizeForCmsModule() throws Exception {
        boolean isAuthorized = apiRequestUtils.fetchObject("/api/auth/cms", Boolean.class);
        assertTrue(isAuthorized);
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK"})
    public void shouldNotAuthorizeForCmsModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/cms");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAuthorizeForAdminPanelModule() throws Exception {
        boolean isAuthorized = apiRequestUtils.fetchObject("/api/auth/admin", Boolean.class);
        assertTrue(isAuthorized);
    }

    @Test
    @WithMockUser(roles = {"WAITER", "COOK", "MANAGER"})
    public void shouldNotAuthorizeForAdminPanelModule() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/auth/admin");
    }
}