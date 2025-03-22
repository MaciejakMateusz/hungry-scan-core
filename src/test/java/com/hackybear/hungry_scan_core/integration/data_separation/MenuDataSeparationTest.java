package com.hackybear.hungry_scan_core.integration.data_separation;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuDataSeparationTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void getMenus_1stUser() throws Exception {
        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/menus", MenuSimpleDTO.class).stream().toList();

        assertEquals(1, menus.size());
        assertEquals("Całodniowe", menus.getFirst().name());
        assertTrue(menus.getFirst().standard());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void getMenus_2ndUser() throws Exception {
        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/menus", MenuSimpleDTO.class).stream().toList();

        assertEquals(2, menus.size());
        assertEquals("Dzienne", menus.getFirst().name());
        assertFalse(menus.getFirst().standard());
        assertEquals("Wieczorne", menus.get(1).name());
        assertFalse(menus.get(1).standard());
    }

}
