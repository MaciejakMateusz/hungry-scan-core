package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.utility.TimeRange;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private UserRepository userRepository;

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
        assertTrue(menus.getFirst().standard());
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
        assertEquals("Śniadaniowe", menu.name());
        assertFalse(menu.standard());
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
        User user = userRepository.findByUsername("admin@example.com").orElseThrow();
        assertEquals(1L, user.getActiveMenuId());
        MenuSimpleDTO menu = createMenuDTO("Great test menu");
        apiRequestUtils.postAndExpect200("/api/cms/menus/add", menu);


        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsList(
                        "/api/cms/menus", MenuSimpleDTO.class);
        MenuSimpleDTO newMenu = menus.stream().filter(m -> m.name().equals("Great test menu")).toList().getFirst();
        assertNotNull(newMenu);
        assertFalse(newMenu.standard());
        user = userRepository.findByUsername("admin@example.com").orElseThrow();
        assertEquals(newMenu.id(), user.getActiveMenuId());
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
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldSwitchStandard() throws Exception {
        Menu existing = getMenu(2L);
        assertEquals("Menu", existing.getName());
        assertTrue(existing.isStandard());

        apiRequestUtils.patchAndExpect200("/api/cms/menus/switch-standard", 4L);

        Menu oldStandard = getMenu(2L);
        assertEquals("Menu", oldStandard.getName());
        assertFalse(oldStandard.isStandard());

        Menu newStandard = getMenu(4L);
        assertEquals("Obiadowe", newStandard.getName());
        assertTrue(newStandard.isStandard());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToSwitchStandard() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/menus/switch-standard", 2);
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

        existingMenu = menuMapper.toSimpleDTO(menu);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/update", existingMenu);

        Menu updatedMenu = getMenu(1L);
        assertEquals("Great test menu", updatedMenu.getName());
        assertTrue(updatedMenu.isStandard());
        assertNotNull(updatedMenu.getUpdated());
        assertEquals("admin@example.com", updatedMenu.getModifiedBy());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldUpdatePlans() throws Exception {
        List<DayOfWeek> allDays = List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY);
        Menu standardMenu = getMenu(2L);
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(LocalTime.of(8, 0), LocalTime.of(12, 0), allDays));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(LocalTime.of(12, 0), LocalTime.of(18, 0), allDays));
        Menu menu3 = getMenu(5L);
        menu3.setPlan(getPlan(LocalTime.of(18, 0), LocalTime.of(22, 0), allDays));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(standardMenu);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto3 = menuMapper.toSimpleDTO(menu2);
        MenuSimpleDTO dto4 = menuMapper.toSimpleDTO(menu3);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2, dto3, dto4);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/update-plans", menuSimpleDTOs);

        List<MenuSimpleDTO> updatedMenus = apiRequestUtils.fetchAsList("/api/cms/menus", MenuSimpleDTO.class);
        assertFalse(updatedMenus.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotUpdatePlans() throws Exception {
        List<DayOfWeek> allDays = List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY);
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(LocalTime.of(8, 0), LocalTime.of(13, 0), allDays));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(LocalTime.of(12, 0), LocalTime.of(18, 0), allDays));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu2);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2);

        Map<?, ?> error = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update-plans", menuSimpleDTOs);
        assertEquals(1, error.size());
        assertEquals("Harmonogramy nie mogą na siebie nachodzić.", error.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToUpdatePlans() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/menus/update-plans", List.of());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdatePlan_whenTimeMatchesOpeningAndClosingHours() throws Exception {
        shouldUpdateSchedule(LocalTime.of(7, 0), LocalTime.of(23, 0));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdatePlan_whenTimeIsFullyWithinOpeningHours() throws Exception {
        shouldUpdateSchedule(LocalTime.of(9, 0), LocalTime.of(21, 0));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdatePlan_whenStartTimeMatchesOpeningHour() throws Exception {
        shouldUpdateSchedule(LocalTime.of(7, 0), LocalTime.of(20, 0));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdatePlan_whenEndTimeMatchesClosingHour() throws Exception {
        shouldUpdateSchedule(LocalTime.of(9, 0), LocalTime.of(23, 0));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotUpdatePlan_whenTimeIsCompletelyOutsideOpeningHours() throws Exception {
        shouldNotUpdateSchedule(LocalTime.of(5, 0), LocalTime.of(6, 30));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotUpdatePlan_whenTimeStartsBeforeOpeningHour() throws Exception {
        shouldNotUpdateSchedule(LocalTime.of(5, 0), LocalTime.of(22, 30));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotUpdatePlan_whenTimeEndsAfterClosingHour() throws Exception {
        shouldNotUpdateSchedule(LocalTime.of(10, 30), LocalTime.of(23, 30));
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToUpdate() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/menus/update", new Menu());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void shouldNotUpdateIncorrect() throws Exception {
        Menu existingMenu = getMenu(2L);
        existingMenu.setName("");
        MenuSimpleDTO menuDTO = menuMapper.toSimpleDTO(existingMenu);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update", menuDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldRemove() throws Exception {
        Optional<Menu> existingMenu = menuRepository.findById(3L);
        assertTrue(existingMenu.isPresent());
        apiRequestUtils.deleteAndExpect200("/api/cms/menus/delete");

        existingMenu = menuRepository.findById(3L);
        assertFalse(existingMenu.isPresent());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotRemoveStandard() throws Exception {
        Menu existingMenu = getMenu(1L);
        assertEquals("Całodniowe", existingMenu.getName());

        apiRequestUtils.deleteAndExpect("/api/cms/menus/delete", 1, status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToRemove() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/menus/delete", 5, status().isForbidden());
    }

    private void shouldUpdateSchedule(LocalTime startTime, LocalTime endTime) throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        assertEquals("Całodniowe", existingMenu.name());
        Menu menu = menuMapper.toMenu(existingMenu);
        menu.setPlan(getPlan(startTime, endTime, List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY)));
        menu.setStandard(false);
        existingMenu = menuMapper.toSimpleDTO(menu);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/update", existingMenu);

        Menu updatedMenu = getMenu(1L);
        assertEquals("Całodniowe", updatedMenu.getName());
        assertFalse(updatedMenu.isStandard());
        assertNotNull(updatedMenu.getUpdated());
        assertEquals("admin@example.com", updatedMenu.getModifiedBy());

        Map<DayOfWeek, TimeRange> updatedPlan = updatedMenu.getPlan();
        assertEquals(7, updatedPlan.size());
        assertTrue(updatedPlan.containsKey(DayOfWeek.SATURDAY));
        assertEquals(startTime, updatedPlan.get(DayOfWeek.SATURDAY).getStartTime());
        assertEquals(endTime, updatedPlan.get(DayOfWeek.SATURDAY).getEndTime());
    }


    private void shouldNotUpdateSchedule(LocalTime startTime, LocalTime endTime) throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        Menu menu = menuMapper.toMenu(existingMenu);
        menu.setPlan(getPlan(startTime, endTime, List.of(DayOfWeek.MONDAY)));
        menu.setStandard(false);
        existingMenu = menuMapper.toSimpleDTO(menu);

        Map<?, ?> response =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/cms/menus/update", existingMenu, status().isBadRequest());
        assertEquals(1, response.size());
        assertEquals("Harmonogram nie mieści się w godzinach otwarcia restauracji.", response.get("exceptionMsg"));
    }

    private MenuSimpleDTO createMenuDTO(String name) {
        return new MenuSimpleDTO(null, name, null, false);
    }

    private Map<DayOfWeek, TimeRange> getPlan(LocalTime startTime, LocalTime endTime, List<DayOfWeek> daysOfWeek) {
        Map<DayOfWeek, TimeRange> plan = new HashMap<>();
        TimeRange timeRange = new TimeRange(startTime, endTime);
        daysOfWeek.forEach(day -> plan.put(day, timeRange));
        return plan;
    }

    private Menu getMenu(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow();
        entityManager.detach(menu);
        return menu;
    }

}