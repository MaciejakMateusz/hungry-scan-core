package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import jakarta.persistence.EntityManager;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MenuRepository menuRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Order(2)
    void shouldGetAll() throws Exception {
        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/menus", MenuSimpleDTO.class).stream().toList();

        assertEquals(1, menus.size());
        assertEquals("Całodniowe", menus.getFirst().name());
        assertTrue(menus.getFirst().allDay());
    }

    @Test
    @WithMockUser(roles = {"WAITER"}, username = "matimemek@test.com")
    void shouldForbidToGetAll() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/menus");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void shouldShowById() throws Exception {
        MenuSimpleDTO menu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 3, MenuSimpleDTO.class);
        assertEquals("Wieczorne", menu.name());
        assertFalse(menu.allDay());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    void shouldForbidToShowById() throws Exception {
        Map<?, ?> response = apiRequestUtils.postAndReturnResponseBody(
                "/api/cms/menus/show", 1, status().isBadRequest());
        assertEquals(1, response.size());
        assertEquals("Brak autoryzacji do wykonania tej operacji.", response.get("exceptionMsg"));
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShow() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/menus/show", 2);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldNotShowNonExisting() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/menus/show", 55, status().isBadRequest());
        assertEquals("Menu z podanym ID nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNew() throws Exception {
        MenuSimpleDTO menu = createMenuDTO("Great test menu");
        apiRequestUtils.postAndExpect200("/api/cms/menus/add", menu);

        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsList(
                        "/api/cms/menus", MenuSimpleDTO.class);
        MenuSimpleDTO newMenu = menus.stream().filter(m -> m.name().equals("Great test menu")).toList().getFirst();
        assertNotNull(newMenu);
        assertTrue(newMenu.allDay());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToAdd() throws Exception {
        MenuSimpleDTO menu = createMenuDTO("Test");
        apiRequestUtils.postAndExpect("/api/cms/menus/add", menu, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void shouldNotAddWithIncorrectName() throws Exception {
        MenuSimpleDTO menu = createMenuDTO("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/menus/add", menu);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdate() throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        assertEquals("Całodniowe", existingMenu.name());
        Menu menu = menuMapper.toMenu(existingMenu);
        menu.setName("Great test menu");

        existingMenu = menuMapper.toDTO(menu);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/update", existingMenu);

        Menu updatedMenu = getMenu(1L);
        assertEquals("Great test menu", updatedMenu.getName());
        assertTrue(updatedMenu.isAllDay());
        assertNotNull(updatedMenu.getUpdated());
        assertEquals("admin@example.com", updatedMenu.getModifiedBy());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToUpdate() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/menus/update", new Menu());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotUpdateIncorrect() throws Exception {
        Menu existingMenu = getMenu(1L);
        existingMenu.setName("");
        MenuSimpleDTO menuDTO = menuMapper.toDTO(existingMenu);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update", menuDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemove() throws Exception {
        Menu existingMenu = getMenu(1L);
        assertEquals("Całodniowe", existingMenu.getName());

        apiRequestUtils.deleteAndExpect200("/api/cms/menus/delete", 1);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/menus/show", 1, status().isBadRequest());
        assertEquals("Menu z podanym ID nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToRemove() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/menus/delete", 5, status().isForbidden());
    }

    private MenuSimpleDTO createMenuDTO(String name) {
        return new MenuSimpleDTO(null, name, null, true);
    }

    private Menu getMenu(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow();
        entityManager.detach(menu);
        return menu;
    }
}