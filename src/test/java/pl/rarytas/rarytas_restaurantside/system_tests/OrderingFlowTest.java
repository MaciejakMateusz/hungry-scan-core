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
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RoleService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiJwtRequestUtils;
import pl.rarytas.rarytas_restaurantside.test_utils.OrderProcessor;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST SYSTEMOWY
 * I Faza przygotowania
 * 1. Logowanie administratora w systemie
 * 2. Stworzenie konta kelnera Radka w systemie
 * 3. Stworzenie konta kucharza Darka w systemie
 * 4. Stworzenie konta kelnerki Anity w systemie
 * 5. Stworzenie konta kucharza Tomka w systemie
 * II Faza logowania personelu i autoryzacji klienta
 * 6. Logowanie kelnera Radka do systemu
 * 7. Logowanie kucharza Darka do systemu
 * 8. Logowanie kelnerki Anity do systemu
 * 9. Logowanie kucharza Tomka do systemu
 * 10. Aktywacja stolika 9 przez Radka
 * 11. Skan QR stolika 9 i uzyskanie uprawnień klienta do aplikacji
 * 12. Aktywacja stolika 10 przez Anitę
 * 13. Skan QR stolika 10 i uzyskanie uprawnień klienta do aplikacji
 * III Faza zamówień
 * 14. Zamówienie kilku dań przez stolik 9
 * 15. Wezwanie kelnera do stolika 10
 * 16. Potwierdzenie obsługi klienta przez Anitę
 * 17. Zamówienie kilku dań przez stolik 10
 * 18. Domówienie nowych dań przez stolik 9
 * 19. Wezwanie kelnera do stolika 9
 * 20. Potwierdzenie obsługi klienta przez Radka
 * IV Faza finalizacji zamówień
 * 21. Poproszenie o rachunek przez stolik 9 razem z napiwkiem
 * 22. Potwierdzenie zapłaty przez Radka
 * 23. Poproszenie o rachunek przez stolik 10 bez napiwku
 * 24. Dezaktywacja stolika przez Radka
 * 25. Potwierdzenie zapłaty przez Anitę
 * 26. Otrzymanie feedback od stolika 10
 * 27. Dezaktywacja stolika przez Anitę
 **/

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
    private HistoryOrderService historyOrderService;

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
    @Order(12)
    public void table10ActivationBySecondWaiter() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 10, JWTs.get("AnitaWaiter"));
        RestaurantTable table9 = restaurantTableService.findById(10);
        assertTrue(table9.isActive());
    }

    @Test
    @Order(13)
    public void table10ScannedByCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam(
                        "/api/scan/" + restaurantTable.getToken(), JwtResponseDTO.class);
        JWTs.put("customerT10", jwtResponseDTO.getAccessToken());
    }

    @Test
    @Order(14)
    public void table9InitialOrder() throws Exception {
        pl.rarytas.rarytas_restaurantside.entity.Order order =
                orderProcessor.createDineInOrder(9, List.of(4, 12, 15));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order, JWTs.get("customerT9"));

        pl.rarytas.rarytas_restaurantside.entity.Order persistedOrder = orderService.findById(6L);

        assertEquals(9, persistedOrder.getRestaurantTable().getId());
        assertEquals(Money.of(85.00), persistedOrder.getTotalAmount());
    }

    @Test
    @Order(15)
    public void table10WaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 10, JWTs.get("customerT10"));
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        assertTrue(restaurantTable.isWaiterCalled());
    }

    @Test
    @Order(16)
    public void table10ResolveWaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/resolve-call", 10, JWTs.get("AnitaWaiter"));
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        assertFalse(restaurantTable.isWaiterCalled());
    }

    @Test
    @Order(17)
    public void table10InitialOrder() throws Exception {
        pl.rarytas.rarytas_restaurantside.entity.Order order =
                orderProcessor.createDineInOrder(10, List.of(2, 5, 15, 18, 25, 30));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order, JWTs.get("customerT10"));

        pl.rarytas.rarytas_restaurantside.entity.Order persistedOrder = orderService.findById(7L);

        assertEquals(10, persistedOrder.getRestaurantTable().getId());
        assertEquals(Money.of(154.89), persistedOrder.getTotalAmount());
    }

    @Test
    @Order(18)
    public void table9OrderedMoreDishes() throws Exception {
        pl.rarytas.rarytas_restaurantside.entity.Order order =
                orderProcessor.createDineInOrder(9, List.of(4, 12, 15));
        order.setId(6L);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders", order, JWTs.get("customerT9"));

        pl.rarytas.rarytas_restaurantside.entity.Order persistedOrder = orderService.findById(6L);

        assertEquals(9, persistedOrder.getRestaurantTable().getId());
        assertEquals(Money.of(170.00), persistedOrder.getTotalAmount());
    }

    @Test
    @Order(19)
    public void table9WaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 9, JWTs.get("customerT9"));
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        assertTrue(restaurantTable.isWaiterCalled());
    }

    @Test
    @Order(20)
    public void table9ResolveWaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/resolve-call", 9, JWTs.get("RadekWaiter"));
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        assertFalse(restaurantTable.isWaiterCalled());
    }

    @Test
    @Order(21)
    public void table9RequestBillAndTip() throws Exception {
        apiRequestUtils.patchAndExpect200(
                "/api/restaurant/tables/request-bill", 9, PaymentMethod.CARD, JWTs.get("customerT9"));
        apiRequestUtils.patchAndExpect200(
                "/api/restaurant/orders/tip", 6L, Money.of(20.00), JWTs.get("customerT9"));
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        pl.rarytas.rarytas_restaurantside.entity.Order existingOrder = orderService.findById(6L);

//        assertEquals(PaymentMethod.CARD, existingOrder.getPaymentMethod());
        assertTrue(restaurantTable.isBillRequested());
        assertEquals(Money.of(190.00), existingOrder.getTotalAmount());
    }

    @Test
    @Order(22)
    public void table9PaymentConfirmation() throws Exception {
        apiRequestUtils.postAndExpect200(
                "/api/restaurant/orders/finalize-dine-in", 6L, JWTs.get("RadekWaiter"));
        assertThrows(LocalizedException.class, () -> orderService.findById(6L));
    }

    @Test
    @Order(23)
    public void table10RequestBill() throws Exception {
        apiRequestUtils.patchAndExpect200(
                "/api/restaurant/tables/request-bill", 10, PaymentMethod.CASH, JWTs.get("customerT10"));
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        pl.rarytas.rarytas_restaurantside.entity.Order existingOrder = orderService.findById(7L);

//        assertEquals(PaymentMethod.CASH, existingOrder.getPaymentMethod());
        assertTrue(restaurantTable.isBillRequested());
        assertEquals(Money.of(154.89), existingOrder.getTotalAmount());
    }

    @Test
    @Order(24)
    public void table9DeactivationByFirstWaiter() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 9, JWTs.get("RadekWaiter"));
        RestaurantTable table9 = restaurantTableService.findById(9);
        assertFalse(table9.isActive());
    }

    @Test
    @Order(25)
    public void table10PaymentConfirmation() throws Exception {
        apiRequestUtils.postAndExpect200(
                "/api/restaurant/orders/finalize-dine-in", 7L, JWTs.get("AnitaWaiter"));
        assertThrows(LocalizedException.class, () -> orderService.findById(7L));
    }

    @Test
    @Order(27)
    public void table10DeactivationBySecondWaiter() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 10, JWTs.get("AnitaWaiter"));
        RestaurantTable table10 = restaurantTableService.findById(10);
        assertFalse(table10.isActive());
    }

    private User createUser(String username, String email, String password, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRepeatedPassword(password);
        user.setRoles(new HashSet<>(Collections.singletonList(getRoleByName(roleName))));
        return user;
    }

    private Role getRoleByName(String roleName) {
        return roleService.findByName(roleName);
    }
}