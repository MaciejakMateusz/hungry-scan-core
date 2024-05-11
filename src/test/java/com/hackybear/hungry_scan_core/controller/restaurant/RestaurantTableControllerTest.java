package com.hackybear.hungry_scan_core.controller.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantTableControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Autowired
    MockMvc mockMvc;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllFromEndpoint() throws Exception {
        List<RestaurantTable> restaurantTables =
                apiRequestUtils.fetchAsList(
                        "/api/restaurant/tables", RestaurantTable.class);

        assertEquals(19, restaurantTables.size());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToTables() throws Exception {
        mockMvc.perform(get("/api/restaurant/tables"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldGetByIdFromEndpoint() throws Exception {
        RestaurantTable table =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 5, RestaurantTable.class);
        assertTrue(table.isActive());
        assertEquals(5, table.getId());
        assertEquals("58d77e24-6b8c-41a9-b24c-a67602deacdd", table.getToken());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowTable() throws Exception {
        Integer id = 12;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/restaurant/tables/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Transactional
    @Rollback
    void shouldToggleTableActivation() throws Exception {
        RestaurantTable table3 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 3, RestaurantTable.class);
        assertFalse(table3.isActive());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 3);

        table3 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 3, RestaurantTable.class);
        assertTrue(table3.isActive());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 3);

        table3 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 3, RestaurantTable.class);
        assertFalse(table3.isActive());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToToggleTableActivation() throws Exception {
        Integer id = 2;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(patch("/api/restaurant/tables/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isUnauthorized());
    }

//    @Test
//    @WithMockUser(roles = {"CUSTOMER"})
//    @Transactional
//    @Rollback
//    public void shouldRequestBill() throws Exception {
//        RestaurantTable table1 =
//                apiRequestUtils.postObjectExpect200(
//                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
//        Order existingOrder =
//                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
//        assertFalse(table1.isBillRequested());
//        assertEquals(PaymentMethod.NONE, existingOrder.getPaymentMethod());
//
//        apiRequestUtils.patchAndExpect(
//                "/api/restaurant/tables/request-bill", 1L, PaymentMethod.CASH, status().isOk());
//
//        table1 =
//                apiRequestUtils.postObjectExpect200(
//                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
//        existingOrder =
//                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
//        assertTrue(table1.isBillRequested());
//        assertEquals(PaymentMethod.CASH, existingOrder.getPaymentMethod());
//    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    public void shouldCallWaiter() throws Exception {
        RestaurantTable table1 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
        assertFalse(table1.isWaiterCalled());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 1L);

        table1 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
        assertTrue(table1.isWaiterCalled());
    }

//    @Test
//    @WithMockUser(roles = {"CUSTOMER"})
//    @Transactional
//    @Rollback
//    public void shouldNotCallWaiterSecondTime() throws Exception {
//        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 1L);
//
//        Map<?, ?> errors =
//                apiRequestUtils.patchAndReturnResponseBody(
//                        "/api/restaurant/tables/call-waiter", 1L, status().isBadRequest());
//
//        assertEquals(1, errors.size());
//        assertEquals("Stolik z numerem = 1 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
//                errors.get("exceptionMsg"));
//    }

//    @Test
//    @WithMockUser(roles = {"CUSTOMER"})
//    @Transactional
//    @Rollback
//    public void shouldNotCallWaiterWithDeactivatedTable() throws Exception {
//        RestaurantTable table3 =
//                apiRequestUtils.postObjectExpect200(
//                        "/api/restaurant/tables/show", 3, RestaurantTable.class);
//        assertFalse(table3.isActive());
//
//        Map<?, ?> errors =
//                apiRequestUtils.patchAndReturnResponseBody(
//                        "/api/restaurant/tables/call-waiter", 3L, status().isBadRequest());
//
//        assertEquals(1, errors.size());
//        assertEquals("Stolik z numerem = 3 nie jest aktywny.",
//                errors.get("exceptionMsg"));
//    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    public void shouldResolveWaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 1L);
        RestaurantTable table1 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
        assertTrue(table1.isWaiterCalled());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/resolve-call", 1L);

        table1 =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/tables/show", 1, RestaurantTable.class);
        assertFalse(table1.isWaiterCalled());
    }

//    @Test
//    @WithMockUser(roles = {"CUSTOMER"})
//    @Transactional
//    @Rollback
//    public void shouldNotRequestBillWhenWaiterCalled() throws Exception {
//        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 1L);
//
//        Map<?, ?> errors =
//                apiRequestUtils.patchAndReturnResponseBody(
//                        "/api/restaurant/tables/request-bill", 1L, PaymentMethod.CARD, status().isBadRequest());
//
//        assertEquals(1, errors.size());
//        assertEquals("Stolik z numerem = 1 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
//                errors.get("exceptionMsg"));
//    }

    @Test
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/tables/call-waiter", 6L);
        apiRequestUtils.patchAndExpect(
                "/api/restaurant/tables/request-bill", 6L, PaymentMethod.CARD, status().isUnauthorized());
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/tables/resolve-call", 6L);
    }
}