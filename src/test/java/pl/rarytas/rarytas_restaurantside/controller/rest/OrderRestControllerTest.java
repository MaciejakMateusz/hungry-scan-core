package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderServiceInterface;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderServiceInterface orderService;

    @Test
    @Order(1)
    public void shouldGetAllNotPaidFromDB() {
        List<pl.rarytas.rarytas_restaurantside.entity.Order> orders = orderService.findAllNotPaid();

        boolean isPaid = false;
        for (pl.rarytas.rarytas_restaurantside.entity.Order order : orders) {
            if (order.isPaid()) {
                isPaid = true;
                break;
            }
        }
        assertFalse(isPaid);
    }

    @Test
    @Order(2)
    public void shouldGetAllNotPaidFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"paid\":true"));
    }
}
