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

    @Test
    public void shouldGetAllFromDB() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(6, orderedItems.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orderedItems")).andReturn();
        String actualOrderedItemJson = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2,\"readyToServe\":false},{\"id\":2,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2,\"readyToServe\":false},{\"id\":3,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2,\"readyToServe\":false},{\"id\":4,\"menuItem\":{\"id\":2,\"name\":\"Carpaccio z polÄ\u0099dwicy woÅ\u0082owej\",\"description\":\"Cienko pokrojona polÄ\u0099dwica woÅ\u0082owa podana z rukolÄ\u0085, parmezanem i kaparami.\",\"ingredients\":\"PolÄ\u0099dwica woÅ\u0082owa, rukola, parmezan, kapary, oliwa z oliwek\",\"price\":24.50,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":3,\"readyToServe\":false},{\"id\":5,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2,\"readyToServe\":false},{\"id\":6,\"menuItem\":{\"id\":5,\"name\":\"Nachos z sosem serowym\",\"description\":\"ChrupiÄ\u0085ce nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.\",\"ingredients\":\"Nachos, ser, Å\u009Bmietana, awokado, pomidory, cebula, papryczki chili\",\"price\":16.99,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":4,\"readyToServe\":false}]",
                actualOrderedItemJson);
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
