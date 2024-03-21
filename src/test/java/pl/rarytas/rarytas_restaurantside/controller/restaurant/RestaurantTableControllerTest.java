package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestaurantTableControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllFromEndpoint() throws Exception {
        List<RestaurantTable> restaurantTables =
                apiRequestUtils.fetchAsList(
                        "/api/restaurant/tables", RestaurantTable.class);

        assertEquals(19, restaurantTables.size());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToTables() throws Exception {
        mockMvc.perform(get("/api/restaurant/tables"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldGetByIdFromEndpoint() throws Exception {
        RestaurantTable table =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 5, RestaurantTable.class);
        assertTrue(table.isActive());
        assertEquals(5, table.getId());
        assertEquals("58d77e24-6b8c-41a9-b24c-a67602deacdd", table.getToken());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowTable() throws Exception {
        Integer id = 12;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/restaurant/tables/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Transactional
    @Rollback
    void shouldToggleTableActivation() throws Exception {
        RestaurantTable table2 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 2, RestaurantTable.class);
        assertFalse(table2.isActive());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 2);

        RestaurantTable activatedTable2 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 2, RestaurantTable.class);
        assertTrue(activatedTable2.isActive());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 2);

        RestaurantTable deactivatedTable2 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 2, RestaurantTable.class);
        assertFalse(deactivatedTable2.isActive());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToToggleTableActivation() throws Exception {
        Integer id = 2;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(patch("/api/restaurant/tables/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }
}