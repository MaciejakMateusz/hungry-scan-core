package pl.rarytas.rarytas_restaurantside.system_tests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.dto.AuthRequestDTO;
import pl.rarytas.rarytas_restaurantside.dto.JwtResponseDTO;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RoleService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiJwtRequestUtils;
import pl.rarytas.rarytas_restaurantside.test_utils.OrderProcessor;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderingFlowTest {

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    private final Map<String, String> JWTs = new HashMap<>();

    @Test
    @Order(1)
    public void adminLogin() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("admin", "Testpassword123?");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        this.JWTs.put("admin", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(2)
    public void addFirstWaiter() throws Exception {
        User user = createUser(
                "RadekWaiter", "radek@waiter.pl", "Radek123!", "ROLE_WAITER");

        apiRequestUtils.postAndExpect200("/api/admin/users/add", user, JWTs.get("admin"));

        User persistedUser = userService.findById(6);
        assertEquals("RadekWaiter", persistedUser.getUsername());
        assertEquals("ROLE_WAITER", user.getRoles().stream().findFirst().orElseThrow().getName());
    }

    @Test
    @Order(3)
    public void addFirstCook() throws Exception {
        User user = createUser(
                "DarekCook", "darek@cook.pl", "Darek123!", "ROLE_COOK");

        apiRequestUtils.postAndExpect200("/api/admin/users/add", user, JWTs.get("admin"));

        User persistedUser = userService.findById(7);
        assertEquals("DarekCook", persistedUser.getUsername());
        assertEquals("ROLE_COOK", user.getRoles().stream().findFirst().orElseThrow().getName());
    }

    @Test
    @Order(4)
    public void addSecondWaiter() throws Exception {
        User user = createUser(
                "AnitaWaiter", "anita@waiter.pl", "Anita123!", "ROLE_WAITER");

        apiRequestUtils.postAndExpect200("/api/admin/users/add", user, JWTs.get("admin"));

        User persistedUser = userService.findById(8);
        assertEquals("AnitaWaiter", persistedUser.getUsername());
        assertEquals("ROLE_WAITER", user.getRoles().stream().findFirst().orElseThrow().getName());
    }

    @Test
    @Order(5)
    public void addSecondCook() throws Exception {
        User user = createUser(
                "TomekCook", "tomek@cook.pl", "Tomek123!", "ROLE_COOK");

        apiRequestUtils.postAndExpect200("/api/admin/users/add", user, JWTs.get("admin"));

        User persistedUser = userService.findById(9);
        assertEquals("TomekCook", persistedUser.getUsername());
        assertEquals("ROLE_COOK", user.getRoles().stream().findFirst().orElseThrow().getName());
    }

    @Test
    @Order(6)
    public void firstWaiterLogin() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("RadekWaiter", "Radek123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        this.JWTs.put("RadekWaiter", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(7)
    public void firstCookLogin() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("DarekCook", "Darek123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        this.JWTs.put("DarekCook", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(8)
    public void secondWaiterLogin() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("AnitaWaiter", "Anita123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        this.JWTs.put("AnitaWaiter", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(9)
    public void secondCookLogin() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("TomekCook", "Tomek123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        this.JWTs.put("TomekCook", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(10)
    public void table9ActivationByFirstWaiter() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 9, JWTs.get("RadekWaiter"));
        RestaurantTable table9 = restaurantTableService.findById(9);
        assertTrue(table9.isActive());
    }

    @Test
    @Order(11)
    public void table9ScannedByCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam(
                        "/api/scan/" + restaurantTable.getToken(), JwtResponseDTO.class);
        JWTs.put("customerT9", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(11)
    public void table10ActivationBySecondWaiter() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 10, JWTs.get("AnitaWaiter"));
        RestaurantTable table9 = restaurantTableService.findById(10);
        assertTrue(table9.isActive());
    }

    @Test
    @Order(12)
    public void table10ScannedByCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam(
                        "/api/scan/" + restaurantTable.getToken(), JwtResponseDTO.class);
        JWTs.put("customerT10", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(13)
    public void table9InitialOrder() throws Exception {
        pl.rarytas.rarytas_restaurantside.entity.Order order =
                orderProcessor.createDineInOrder(9, List.of(4, 12, 15));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order, JWTs.get("customerT9"));

        pl.rarytas.rarytas_restaurantside.entity.Order persistedOrder = orderService.findById(6L);

        assertEquals(9, persistedOrder.getRestaurantTable().getId());
        assertEquals(Money.of(63.25), persistedOrder.getTotalAmount());
    }

    //TODO tragiczny błąd w założeniach - przenieś pola isWaiterCalled oraz isBillRequested od obiektu stolika
//    @Test
//    @Order(14)
//    public void table10WaiterCall() throws Exception {
//        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", 7L, JWTs.get("customerT10"));
//        pl.rarytas.rarytas_restaurantside.entity.Order existingOrder = orderService.findById(7L);
//        assertTrue(existingOrder.isWaiterCalled());
//    }

    private User createUser(String username, String email, String password, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRepeatedPassword(password);
        user.setRoles(new HashSet<>(Collections.singletonList(createRole(roleName))));
        return user;
    }

    private Role createRole(String roleName) {
        return roleService.findByName(roleName);
    }
}