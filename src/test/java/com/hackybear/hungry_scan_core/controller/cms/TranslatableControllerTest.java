package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.Translatable;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TranslatableControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private TranslatableMapper translatableMapper;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSaveAll() throws Exception {
        MenuItemFormDTO menuItem = apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItemFormDTO.class);
        assertNull(menuItem.name().en());
        assertNull(menuItem.description().en());

        Translatable nameTranslation = translatableMapper.toTranslatable(menuItem.name());
        nameTranslation.setEn("English name");

        Translatable descriptionTranslation = translatableMapper.toTranslatable(menuItem.description());
        descriptionTranslation.setEn("English description");

        List<Translatable> translatables = List.of(nameTranslation, descriptionTranslation);

        apiRequestUtils.postAndExpect200("/api/cms/translatable/save-all", translatables);

        MenuItemFormDTO translatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItemFormDTO.class);
        assertEquals("English name", translatedMenuItem.name().en());
        assertEquals("English description", translatedMenuItem.description().en());
    }

}