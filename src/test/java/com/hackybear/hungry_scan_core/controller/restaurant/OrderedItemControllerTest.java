package com.hackybear.hungry_scan_core.controller.restaurant;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderedItemControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ApiRequestUtils apiRequestUtils;
//
//    @Autowired
//    private MenuItemVariantService variantService;
//
//    @Order(1)
//    @Sql("/data-h2.sql")
//    @Test
//    void init() {
//        log.info("Initializing H2 database...");
//    }
//
//    @Test
//    @WithMockUser(roles = "WAITER")
//    public void shouldGetAllFromEndpoint() throws Exception {
//        List<OrderedItem> orderedItems =
//                apiRequestUtils.fetchAsList(
//                        "/api/restaurant/ordered-items", OrderedItem.class);
//        assertEquals(5, orderedItems.size());
//        assertEquals(4, orderedItems.get(3).getQuantity());
//    }
//
//    @Test
//    void shouldNotAllowUnauthorizedAccessToOrderedItems() throws Exception {
//        mockMvc.perform(get("/api/restaurant/ordered-items"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(roles = "WAITER")
//    public void shouldShowOrderedItemById() throws Exception {
//        OrderedItem orderedItem =
//                apiRequestUtils.postObjectExpect200(
//                        "/api/restaurant/ordered-items/show", 3, OrderedItem.class);
//        assertEquals(2, orderedItem.getQuantity());
//    }
//
//    @Test
//    void shouldNotAllowUnauthorizedAccessToShowOrderedItem() throws Exception {
//        Long id = 4L;
//        ObjectMapper objectMapper = new ObjectMapper();
//        mockMvc.perform(post("/api/restaurant/ordered-items/show")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(id)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(roles = "COOK")
//    @Transactional
//    @Rollback
//    void shouldAddOrderedItems() throws Exception {
//        List<OrderedItem> orderedItems = createOrderedItems();
//
//        apiRequestUtils.postAndExpect200("/api/restaurant/ordered-items", orderedItems);
//
//        List<OrderedItem> persistedOrderedItems =
//                apiRequestUtils.fetchAsList(
//                        "/api/restaurant/ordered-items", OrderedItem.class);
//
//        assertEquals(10, persistedOrderedItems.size());
//        assertEquals("Sałatka z rukolą, serem kozim i suszonymi żurawinami",
//                persistedOrderedItems.get(5).getMenuItemVariant().getName());
//        assertEquals(2, persistedOrderedItems.get(5).getQuantity());
//    }
//
//    @Test
//    void shouldNotAllowUnauthorizedAccessToAddOrderedItems() throws Exception {
//        List<OrderedItem> orderedItems = createOrderedItems();
//        ObjectMapper objectMapper = new ObjectMapper();
//        mockMvc.perform(post("/api/restaurant/ordered-items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderedItems)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void shouldNotAllowUnauthorizedAccessToToggleIsReadyToServe() throws Exception {
//        Long id = 6L;
//        ObjectMapper objectMapper = new ObjectMapper();
//        mockMvc.perform(patch("/api/restaurant/ordered-items/toggle-item")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(id)))
//                .andExpect(status().isUnauthorized());
//    }
//
//    private List<OrderedItem> createOrderedItems() {
//        List<OrderedItem> orderedItems = new ArrayList<>();
//        int i = 5;
//        while (i > 0) {
//            OrderedItem orderedItem = new OrderedItem();
//            orderedItem.setMenuItemVariant(getMenuItemVariant());
//            orderedItem.setQuantity(2);
//            orderedItems.add(orderedItem);
//            i--;
//        }
//        return orderedItems;
//    }
//
//    private MenuItemVariant getMenuItemVariant() {
//        MenuItemVariant variant;
//        try {
//            variant = variantService.findById(13);
//        } catch (LocalizedException e) {
//            throw new RuntimeException(e);
//        }
//        return variant;
//    }
}
