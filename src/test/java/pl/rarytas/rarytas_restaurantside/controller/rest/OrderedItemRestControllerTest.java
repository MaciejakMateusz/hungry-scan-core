package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderedItemRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderedItemService orderedItemService;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    public void shouldGetAllFromDB() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(6, orderedItems.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        List<OrderedItem> orderedItems =
                apiRequestUtils.fetchItemListFromEndpoint(
                        "/api/orderedItems", OrderedItem.class);
        assertEquals(6, orderedItems.size());
        assertEquals(4, orderedItems.get(5).getQuantity());
    }

    @Test
    public void shouldGetByIdFromDB() {
        OrderedItem orderedItem = orderedItemService.findById(4L).orElse(new OrderedItem());
        //only ordered item with ID = 4 has quantity = 3
        assertEquals(3, orderedItem.getQuantity());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orderedItems/4")).andReturn();
        String actualOrderedItemJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderedItemJson.contains("\"quantity\":3"));
    }

    @Test
    public void shouldUpdateReadyToServe() throws Exception {
        mockMvc.perform(patch("/api/orderedItems")
                        .param("id", String.valueOf(1L))
                        .param("isReadyToServe", String.valueOf(true)))
                .andExpect(status().is2xxSuccessful());

        OrderedItem updatedItem = orderedItemService.findById(1L).orElse(null);
        assertNotNull(updatedItem);
        assertTrue(updatedItem.isReadyToServe());

        mockMvc.perform(patch("/api/orderedItems")
                        .param("id", String.valueOf(1L))
                        .param("isReadyToServe", String.valueOf(false)))
                .andExpect(status().is2xxSuccessful());

        OrderedItem updatedItem2 = orderedItemService.findById(1L).orElse(null);
        assertNotNull(updatedItem2);
        assertFalse(updatedItem2.isReadyToServe());
    }
}
