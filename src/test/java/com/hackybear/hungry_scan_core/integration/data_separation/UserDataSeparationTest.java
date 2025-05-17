package com.hackybear.hungry_scan_core.integration.data_separation;

import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDataSeparationTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void getAllUsers_1stAdmin() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsSet(
                        "/api/admin/users", User.class).stream().toList();

        assertEquals(2, users.size());
        assertEquals("matimemek@test.com", users.getFirst().getUsername());
        assertEquals("netka@test.com", users.getLast().getEmail());
        assertTrue(users.stream().allMatch(user -> user.getOrganizationId() == 1L));
        assertTrue(users.stream().noneMatch(user -> Objects.equals(user.getSurname(), "temp")));
        assertTrue(users.stream().noneMatch(user -> Objects.equals(user.getUsername(), "admin@example.com")));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void getAllUsers_2ndAdmin() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsSet(
                        "/api/admin/users", User.class).stream().toList();

        assertEquals(1, users.size());
        assertEquals("kucharz@antek.pl", users.getFirst().getUsername());
        assertTrue(users.stream().allMatch(user -> user.getOrganizationId() == 2L));
        assertTrue(users.stream().noneMatch(user -> Objects.equals(user.getSurname(), "temp")));
        assertTrue(users.stream().noneMatch(user -> Objects.equals(user.getUsername(), "restaurator@rarytas.pl")));
    }

}