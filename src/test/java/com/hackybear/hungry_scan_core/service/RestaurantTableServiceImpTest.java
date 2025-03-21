package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantTableServiceImpTest {

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    void shouldFindAll() {
        List<RestaurantTable> restaurantTables = restaurantTableService.findAll();
        assertEquals(19, restaurantTables.size());
        assertEquals("5afb9629-990a-4934-87f2-793b1aa2f35e", restaurantTables.get(3).getToken());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(10L);
        assertEquals("88ca9c82-e630-40f2-9bf9-47f7d14f6bff", restaurantTable.getToken());
    }

    @Test
    void shouldNotFindById() {
        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.findById(98L));
        assertEquals("Stolik z ID = 98 nie istnieje.", exception.getLocalizedMessage());
    }

    @Test
    void shouldFindByNumber() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findByNumber(7);
        assertEquals("ef303854-6faa-4615-8d47-6f3686086586", restaurantTable.getToken());
    }

    @Test
    void shouldNotFindByNumber() {
        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.findByNumber(69));
        assertEquals("Stolik z numerem = 69 nie istnieje.", exception.getLocalizedMessage());
    }

    @Test
    void shouldFindByToken() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findByToken("65b6bb94-da99-4ced-8a94-5860fe95e708");
        assertEquals(15, restaurantTable.getId());
    }

    @Test
    void shouldNotFindByToken() {
        assertThrows(LocalizedException.class, () ->
                restaurantTableService.findByToken("65b6bb94-da99-yyymleko-8a94-5860fe95e708"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldCreateNew() throws LocalizedException {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTableService.createNew(restaurantTable);
        RestaurantTable persistedRestaurantTable = restaurantTableService.findById(20L);
        assertEquals(restaurantTable.getToken(), persistedRestaurantTable.getToken());
    }

    @Test
    void shouldNotCreateNew() {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setNumber(5);
        LocalizedException exception = assertThrows(LocalizedException.class, () ->
                restaurantTableService.createNew(restaurantTable));
        assertEquals("Stolik z numerem = 5 już istnieje.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldSave() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(4L);
        restaurantTable.setMaxNumOfPpl(5);
        restaurantTable.setNumber(33);
        restaurantTableService.save(restaurantTable);
        RestaurantTable persistedRestaurantTable = restaurantTableService.findById(4L);
        assertEquals(restaurantTable.getToken(), persistedRestaurantTable.getToken());
        assertEquals(restaurantTable.getNumber(), persistedRestaurantTable.getNumber());
        assertEquals(restaurantTable.getMaxNumOfPpl(), persistedRestaurantTable.getMaxNumOfPpl());
    }

    @Test
    void shouldNotSaveWithIncorrectMaxNumberOfPpl() {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setMaxNumOfPpl(0);
        assertThrows(ConstraintViolationException.class, () -> restaurantTableService.save(restaurantTable));

        restaurantTable.setMaxNumOfPpl(-12);
        assertThrows(ConstraintViolationException.class, () -> restaurantTableService.save(restaurantTable));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDelete() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(4L);
        assertEquals("5afb9629-990a-4934-87f2-793b1aa2f35e", restaurantTable.getToken());

        restaurantTableService.delete(4L);

        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.findById(4L));
        assertEquals("Stolik z ID = 4 nie istnieje.", exception.getLocalizedMessage());
    }

    @Test
    void shouldNotDeleteWithIncorrectId() {
        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.delete(69L));
        assertEquals("Stolik z ID = 69 nie istnieje.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotDeleteWhenTableIsActive() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(4L);
        assertEquals("5afb9629-990a-4934-87f2-793b1aa2f35e", restaurantTable.getToken());

        restaurantTableService.toggleActivation(4L);

        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.delete(4L));
        assertEquals("Stolik z ID = 4 jest jeszcze aktywny.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldGenerateNewToken() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        String originalToken = restaurantTable.getToken();
        assertEquals("fe2cce7c-7c4c-4076-9eb4-3e91b440fec2", originalToken);

        restaurantTableService.generateNewToken(9L);

        restaurantTable = restaurantTableService.findById(9L);
        assertNotEquals(originalToken, restaurantTable.getToken());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotGenerateNewTokenWhenTableIsActive() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        String originalToken = restaurantTable.getToken();

        restaurantTableService.toggleActivation(9L);

        LocalizedException exception = assertThrows(LocalizedException.class, () ->
                restaurantTableService.generateNewToken(9L));
        assertEquals("Stolik z ID = 9 jest jeszcze aktywny.", exception.getLocalizedMessage());
        assertEquals(originalToken, restaurantTable.getToken());
    }

    @Test
    @Transactional
    @Rollback
    void shouldToggleActivation() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        assertFalse(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9L);
        restaurantTable = restaurantTableService.findById(9L);
        assertTrue(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9L);
        restaurantTable = restaurantTableService.findById(9L);
        assertFalse(restaurantTable.isActive());
    }

    @Test
    void shouldNotToggleWithIncorrectId() {
        LocalizedException exception = assertThrows(LocalizedException.class, () ->
                restaurantTableService.toggleActivation(55L));
        assertEquals("Stolik z ID = 55 nie istnieje.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldCallWaiter() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        restaurantTableService.toggleActivation(9L);
        assertFalse(restaurantTable.isWaiterCalled());

        restaurantTableService.callWaiter(9L);
        restaurantTable = restaurantTableService.findById(9L);
        assertTrue(restaurantTable.isWaiterCalled());
    }

    @Test
    void shouldNotCallWaiterWhenTableNotActive() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        assertFalse(restaurantTable.isWaiterCalled());

        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.callWaiter(9L));
        assertEquals("Stolik z ID = 9 nie jest aktywny.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotCallWaiterWhenWaiterCalled() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        assertFalse(restaurantTable.isWaiterCalled());

        restaurantTableService.toggleActivation(9L);
        restaurantTableService.callWaiter(9L);
        assertTrue(restaurantTable.isWaiterCalled());

        LocalizedException exception = assertThrows(LocalizedException.class, () -> restaurantTableService.callWaiter(9L));
        assertEquals("Stolik posiada już aktywne wezwanie kelnera lub prośbę o rachunek.", exception.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldResolveWaiterCall() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9L);
        restaurantTableService.toggleActivation(9L);
        restaurantTableService.callWaiter(9L);
        assertTrue(restaurantTable.isWaiterCalled());

        restaurantTableService.resolveWaiterCall(9L);

        RestaurantTable updatedTable = restaurantTableService.findById(9L);
        assertFalse(updatedTable.isWaiterCalled());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotResolveWaiterCallWithIncorrectTableId() {
        LocalizedException exception = assertThrows(LocalizedException.class, () ->
                restaurantTableService.resolveWaiterCall(69L));
        assertEquals("Stolik z ID = 69 nie istnieje.", exception.getLocalizedMessage());
    }

    //TODO shouldNotResolveWaiterCallWithIncorrectTableId()
    //     shouldRequestBill
    //     shouldNotRequestBillWithActiveWaiterCall
    //     shouldNotRequestBillWithActiveBillRequest

    private RestaurantTable createRestaurantTable() {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setToken(UUID.randomUUID().toString());
        restaurantTable.setMaxNumOfPpl(3);
        return restaurantTable;
    }
}