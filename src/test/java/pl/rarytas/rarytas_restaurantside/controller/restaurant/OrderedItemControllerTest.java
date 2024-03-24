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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrderedItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MenuItemService menuItemService;

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllFromEndpoint() throws Exception {
        List<OrderedItem> orderedItems =
                apiRequestUtils.fetchAsList(
                        "/api/restaurant/ordered-items", OrderedItem.class);
        assertEquals(5, orderedItems.size());
        assertEquals(4, orderedItems.get(3).getQuantity());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToOrderedItems() throws Exception {
        mockMvc.perform(get("/api/restaurant/ordered-items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldShowOrderedItemById() throws Exception {
        OrderedItem orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 3, OrderedItem.class);
        assertEquals(2, orderedItem.getQuantity());
    }

    @Test
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
    @Transactional
    @Rollback
    void shouldAddOrderedItems() throws Exception {
        List<OrderedItem> orderedItems = createOrderedItems();

        apiRequestUtils.postAndExpect200("/api/restaurant/ordered-items", orderedItems);

        List<OrderedItem> persistedOrderedItems =
                apiRequestUtils.fetchAsList(
                        "/api/restaurant/ordered-items", OrderedItem.class);

        assertEquals(10, persistedOrderedItems.size());
        assertEquals("Sałatka z rukolą, serem kozim i suszonymi żurawinami",
                persistedOrderedItems.get(5).getMenuItem().getName());
        assertEquals(2, persistedOrderedItems.get(5).getQuantity());
    }

    @Test
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
    @Transactional
    @Rollback
    void shouldToggleIsReadyToServe() throws Exception {
        OrderedItem orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 4, OrderedItem.class);
        assertFalse(orderedItem.isReadyToServe());

        apiRequestUtils.patchAndExpect200("/api/restaurant/ordered-items/toggle-item", 4);

        OrderedItem readyOrderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 4, OrderedItem.class);
        assertTrue(readyOrderedItem.isReadyToServe());

        apiRequestUtils.patchAndExpect200("/api/restaurant/ordered-items/toggle-item", 4);
        orderedItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/ordered-items/show", 4, OrderedItem.class);
        assertFalse(orderedItem.isReadyToServe());
    }

    @Test
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
            orderedItem.setMenuItem(getMenuItem());
            orderedItem.setQuantity(2);
            orderedItems.add(orderedItem);
            i--;
        }
        return orderedItems;
    }

    private MenuItem getMenuItem() {
        MenuItem menuItem;
        try {
            menuItem = menuItemService.findById(13);
        } catch (LocalizedException e) {
            throw new RuntimeException(e);
        }
        return menuItem;
    }
}
