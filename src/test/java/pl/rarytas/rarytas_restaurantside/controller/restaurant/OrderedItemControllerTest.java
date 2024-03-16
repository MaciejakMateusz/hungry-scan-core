package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderedItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(1)
    public void shouldGetAllFromEndpoint() throws Exception {
        List<OrderedItem> orderedItems =
                apiRequestUtils.fetchObjects(
                        "/api/restaurant/ordered-items", OrderedItem.class);
        assertEquals(4, orderedItems.size());
        assertEquals(4, orderedItems.get(3).getQuantity());
    }

    @Test
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToOrderedItems() throws Exception {
        mockMvc.perform(get("/api/restaurant/ordered-items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(3)
    public void shouldShowOrderedItemById() throws Exception {
        OrderedItem orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 3, OrderedItem.class);
        assertEquals(2, orderedItem.getQuantity());
    }

    @Test
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToShowOrderedItem() throws Exception {
        Long id = 4L;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/restaurant/ordered-items/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(5)
    void shouldAddOrderedItems() throws Exception {
        List<OrderedItem> orderedItems = createOrderedItems();

        apiRequestUtils.postAndExpect200("/api/restaurant/ordered-items", orderedItems);

        List<OrderedItem> persistedOrderedItems =
                apiRequestUtils.fetchObjects(
                        "/api/restaurant/ordered-items", OrderedItem.class);

        assertEquals(9, persistedOrderedItems.size());
        assertEquals("Good foot.", persistedOrderedItems.get(5).getMenuItem().getName());
        assertEquals(2, persistedOrderedItems.get(5).getQuantity());
    }

    @Test
    @Order(6)
    void shouldNotAllowUnauthorizedAccessToAddOrderedItems() throws Exception {
        List<OrderedItem> orderedItems = createOrderedItems();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/restaurant/ordered-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderedItems)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(7)
    void shouldToggleIsReadyToServe() throws Exception {
        OrderedItem orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 5, OrderedItem.class);
        assertFalse(orderedItem.isReadyToServe());

        apiRequestUtils.patchAndExpect200("/api/restaurant/ordered-items/toggle-item", 5);

        OrderedItem readyOrderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 5, OrderedItem.class);
        assertTrue(readyOrderedItem.isReadyToServe());

        apiRequestUtils.patchAndExpect200("/api/restaurant/ordered-items/toggle-item", 5);
        orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 5, OrderedItem.class);
        assertFalse(orderedItem.isReadyToServe());
    }

    @Test
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToToggleIsReadyToServe() throws Exception {
        Long id = 6L;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(patch("/api/restaurant/ordered-items/toggle-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }

    private List<OrderedItem> createOrderedItems() {
        List<OrderedItem> orderedItems = new ArrayList<>();
        int i = 5;
        while (i > 0) {
            OrderedItem orderedItem = new OrderedItem();
            orderedItem.setMenuItem(createMenuItem());
            orderedItem.setQuantity(2);
            orderedItems.add(orderedItem);
            i--;
        }
        return orderedItems;
    }

    private MenuItem createMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Good foot.");
        menuItem.setDescription("Foot is good.");
        menuItem.setPrice(BigDecimal.valueOf(12.00));
        return menuItem;
    }
}
