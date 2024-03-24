package pl.rarytas.rarytas_restaurantside.controller.login;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.dto.AuthRequestDTO;
import pl.rarytas.rarytas_restaurantside.dto.JwtResponseDTO;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateAndLoginUser() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("mati", "Lubieplacki123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(129, jwtResponseDTO.getAccessToken().length());
    }

    @Test
    void shouldLoginAndReturnUnauthorized() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("iDoNotExist", "DoesNotMatter123!");
        apiRequestUtils.postAndExpectUnauthorized("/api/login", authRequestDTO);
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateForCollaborativeOrdering() throws Exception {
        shouldAuthenticateQrScanForTable1FirstCustomer();
        shouldAuthenticateQrScanForTable1SecondCustomer();
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateQrScanForTable6AndCallWaiter() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(6);
        restaurantTable.setActive(true);
        restaurantTableService.save(restaurantTable);

        String tableToken = "/59ebc00c-b580-4dff-9788-2df90b1d4bba";

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        Order order = orderService.findById(1L);
        assertFalse(order.isWaiterCalled());

        String orderAsJsonString = apiRequestUtils.prepObjMapper().writeValueAsString(1);

        mockMvc.perform(patch("/api/restaurant/orders/call-waiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponseDTO.getAccessToken())
                        .content(orderAsJsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Order updatedOrder = orderService.findById(1L);
        assertTrue(updatedOrder.isWaiterCalled());
    }

    private void shouldAuthenticateQrScanForTable1FirstCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(1);
        assertFalse(restaurantTable.isActive());
        assertTrue(restaurantTable.getUsers().isEmpty());

        restaurantTable.setActive(true);
        restaurantTableService.save(restaurantTable);

        String tableToken = "/19436a86-e200-400d-aa2e-da4686805d00";

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        restaurantTable = restaurantTableService.findById(1);
        assertEquals(1, restaurantTable.getUsers().size());
        assertEquals(12, restaurantTable.getUsers()
                .stream()
                .findFirst()
                .orElseThrow()
                .getUsername()
                .length());

        assertEquals(20, restaurantTable.getUsers()
                .stream()
                .findFirst()
                .orElseThrow()
                .getEmail()
                .length());
    }

    private void shouldAuthenticateQrScanForTable1SecondCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(1);
        assertTrue(restaurantTable.isActive());
        assertEquals(1, restaurantTable.getUsers().size());

        String tableToken = "/19436a86-e200-400d-aa2e-da4686805d00";

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        restaurantTable = restaurantTableService.findById(1);
        assertEquals(2, restaurantTable.getUsers().size());
        assertEquals(12, restaurantTable.getUsers()
                .stream()
                .skip(1)
                .findFirst()
                .orElseThrow()
                .getUsername()
                .length());

        assertEquals(20, restaurantTable.getUsers()
                .stream()
                .skip(1)
                .findFirst()
                .orElseThrow()
                .getEmail()
                .length());
    }

}