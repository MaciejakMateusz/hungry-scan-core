package pl.rarytas.rarytas_restaurantside.controller.restaurant.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService mockOrderService;

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldReturnTakeAwayView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/take-away"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/orders/take-away"));
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToTakeAwayView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/take-away"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldReturnHistoryDineInView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/finalized"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/orders/history-dineIn"));
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToHistoryDineInView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/finalized"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldReturnHistoryTakeAwayView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/finalized/take-away"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/orders/history-takeAway"));
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToHistoryTakeAwayView() throws Exception {
        mockMvc.perform(get("/restaurant/orders/finalized/take-away"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldFinalizeDineInOrder() throws Exception {
        mockMvc.perform(post("/restaurant/orders/finalize-dineIn")
                        .param("id", "1")
                        .param("paid", "true")
                        .param("isResolved", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant"));

        verify(mockOrderService, times(1)).finish(1L, true, true);
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldFinalizeTakeAwayOrder() throws Exception {
        mockMvc.perform(post("/restaurant/orders/finalize-takeAway")
                        .param("id", "1")
                        .param("paid", "true")
                        .param("isResolved", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/orders/take-away"));

        verify(mockOrderService, times(1)).finishTakeAway(1L, true, true);
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldResolveWaiterCall() throws Exception {
        mockMvc.perform(post("/restaurant/orders/resolve-call")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant"));

        verify(mockOrderService, times(1)).resolveWaiterCall(1L);
    }

    @Test
    void shouldThrow403ForPostRequests() throws Exception {
        mockMvc.perform(post("/restaurant/orders/finalize-dineIn"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/restaurant/orders/finalize-takeAway"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/restaurant/orders/resolve-call"))
                .andExpect(status().isForbidden());
    }
}