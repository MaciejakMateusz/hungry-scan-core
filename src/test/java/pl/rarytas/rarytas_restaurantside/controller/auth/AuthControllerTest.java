package pl.rarytas.rarytas_restaurantside.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        apiRequestUtils.fetchAndExpectUnauthorized("/api/auth/restaurant", status().is(401));
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
        apiRequestUtils.fetchAndExpectUnauthorized("/api/auth/cms", status().is(403));
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
        apiRequestUtils.fetchAndExpectUnauthorized("/api/auth/admin", status().is(403));
    }
}