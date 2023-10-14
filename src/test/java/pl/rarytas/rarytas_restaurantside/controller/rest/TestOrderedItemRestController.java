package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemServiceInterface;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrderedItemRestController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderedItemServiceInterface orderedItemService;

    @Test
    public void shouldGetAllFromDB() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(5, orderedItems.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orderedItems")).andReturn();
        String actualOrderedItemJson = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":\"2023-08-02 04:06:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2},{\"id\":2,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":\"2023-08-02 04:06:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2},{\"id\":3,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":\"2023-08-02 04:06:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2},{\"id\":4,\"menuItem\":{\"id\":2,\"name\":\"Carpaccio z polÄ\u0099dwicy woÅ\u0082owej\",\"description\":\"Cienko pokrojona polÄ\u0099dwica woÅ\u0082owa podana z rukolÄ\u0085, parmezanem i kaparami.\",\"ingredients\":\"PolÄ\u0099dwica woÅ\u0082owa, rukola, parmezan, kapary, oliwa z oliwek\",\"price\":24.50,\"created\":\"2023-08-02 04:06:03\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":3},{\"id\":5,\"menuItem\":{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieÅ\u009Bcie tempura, podawane z sosem sÅ\u0082odko-kwaÅ\u009Bnym\",\"ingredients\":\"Krewetki, mÄ\u0085ka, jajko, olej roÅ\u009Blinny, sos sÅ\u0082odko-kwaÅ\u009Bny\",\"price\":22.00,\"created\":\"2023-08-02 04:06:24\",\"updated\":null,\"base64Image\":\"empty\",\"available\":true},\"quantity\":2}]",
                actualOrderedItemJson);
    }

    @Test
    public void shouldGetByIdFromDB() {
        OrderedItem orderedItem = orderedItemService.findById(4).orElse(new OrderedItem());
        //only ordered item with ID=4 has quantity=3
        assertEquals(3, orderedItem.getQuantity());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orderedItems/4")).andReturn();
        String actualOrderedItemJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderedItemJson.contains("\"quantity\":3"));
    }
}
