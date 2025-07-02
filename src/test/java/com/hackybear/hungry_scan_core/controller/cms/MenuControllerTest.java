package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuColorMapper;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuColor;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuColorRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
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
    private MenuColorMapper menuColorMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuColorRepository menuColorRepository;

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
    void shouldProjectPlannedMenu() throws Exception {
        MenuCustomerDTO menu = apiRequestUtils.fetchObject(
                "/api/cms/menus/customer", MenuCustomerDTO.class);
        assertEquals("Rarytas", menu.restaurant().name());
        assertEquals("#318E41", menu.theme());
        assertEquals("Smacznego!", menu.message().pl());
        assertEquals("Enjoy your meal!", menu.message().en());
        assertEquals(9, menu.categories().size());
        assertEquals(5, menu.categories().getFirst().menuItems().size());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldAddNew() throws Exception {
        User user = userRepository.findByUsername("restaurator@rarytas.pl").orElseThrow();
        assertEquals(3L, user.getActiveMenuId());
        MenuSimpleDTO menu = createMenuDTO("Całodniowe", 2L);
        apiRequestUtils.postAndExpect200("/api/cms/menus/add", menu);


        List<MenuSimpleDTO> menus =
                apiRequestUtils.fetchAsList(
                        "/api/cms/menus", MenuSimpleDTO.class);
        MenuSimpleDTO newMenu = menus.stream().filter(m -> m.name().equals("Całodniowe")).toList().getFirst();
        assertNotNull(newMenu);
        assertFalse(newMenu.standard());
        user = userRepository.findByUsername("restaurator@rarytas.pl").orElseThrow();
        assertEquals(newMenu.id(), user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotAddNonUniqueName() throws Exception {
        User user = userRepository.findByUsername("admin@example.com").orElseThrow();
        assertEquals(1L, user.getActiveMenuId());
        MenuSimpleDTO menu = createMenuDTO("Całodniowe", 1L);
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/menus/add", menu);
        assertEquals(1, errors.size());
        assertEquals("Nazwy menu powinny być unikalne.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToAdd() throws Exception {
        MenuSimpleDTO menu = createMenuDTO("Test", 1L);
        apiRequestUtils.postAndExpect("/api/cms/menus/add", menu, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void shouldNotAddWithIncorrectName() throws Exception {
        MenuSimpleDTO menu = createMenuDTO("", 2L);

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

        apiRequestUtils.patchAndExpect200("/api/user/menu", 4L);
        apiRequestUtils.patchAndExpect200("/api/cms/menus/switch-standard");

        Menu oldStandard = getMenu(2L);
        assertEquals("Menu", oldStandard.getName());
        assertFalse(oldStandard.isStandard());

        Menu newStandard = getMenu(4L);
        assertEquals("Obiadowe", newStandard.getName());
        assertTrue(newStandard.isStandard());
        assertTrue(newStandard.getPlan().isEmpty());
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
        Menu standardMenu = getMenu(2L);
        standardMenu.setPlan(getPlan(standardMenu, Set.of(new TimeRange(
                LocalTime.of(12, 0),
                LocalTime.of(14, 0)))));
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(menu1, Set.of(new TimeRange(
                LocalTime.of(14, 0),
                LocalTime.of(22, 0)))));

        MenuSimpleDTO standardDTO = menuMapper.toSimpleDTO(standardMenu);
        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(standardDTO, dto1);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/update-plans", menuSimpleDTOs);

        List<MenuSimpleDTO> updatedMenus = apiRequestUtils.fetchAsList("/api/cms/menus", MenuSimpleDTO.class);
        assertFalse(updatedMenus.isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotUpdatePlans_overlappingSchedules() throws Exception {
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(menu1, Set.of(new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0)))));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(menu2, Set.of(new TimeRange(LocalTime.of(13, 0), LocalTime.of(22, 0)))));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu2);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2);

        Map<?, ?> error = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update-plans", menuSimpleDTOs);
        assertEquals(1, error.size());
        assertEquals("Harmonogramy nie mogą na siebie nachodzić.", error.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotUpdatePlans_pastOperatingHours() throws Exception {
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(menu1, Set.of(new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0)))));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(menu2, Set.of(new TimeRange(LocalTime.of(14, 0), LocalTime.of(23, 0)))));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu2);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2);

        Map<?, ?> error = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update-plans", menuSimpleDTOs);
        assertEquals(1, error.size());
        assertEquals("Harmonogram nie mieści się w godzinach otwarcia restauracji.", error.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotUpdatePlans_beforeOperatingHours() throws Exception {
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(menu1, Set.of(new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0)))));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(menu2, Set.of(new TimeRange(LocalTime.of(14, 0), LocalTime.of(22, 0)))));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu2);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2);

        Map<?, ?> error = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update-plans", menuSimpleDTOs);
        assertEquals(1, error.size());
        assertEquals("Harmonogram nie mieści się w godzinach otwarcia restauracji.", error.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotUpdatePlans_incompletePlan() throws Exception {
        Menu menu1 = getMenu(3L);
        menu1.setPlan(getPlan(menu1, Set.of(new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0)))));
        Menu menu2 = getMenu(4L);
        menu2.setPlan(getPlan(menu2, Set.of(new TimeRange(LocalTime.of(14, 0), LocalTime.of(20, 0)))));

        MenuSimpleDTO dto1 = menuMapper.toSimpleDTO(menu1);
        MenuSimpleDTO dto2 = menuMapper.toSimpleDTO(menu2);
        List<MenuSimpleDTO> menuSimpleDTOs = List.of(dto1, dto2);

        Map<?, ?> error = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/update-plans", menuSimpleDTOs);
        assertEquals(1, error.size());
        assertEquals("Harmonogram jest niekompletny, wypełnij wszystkie luki.", error.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "WAITER", username = "matimemek@test.com")
    void shouldNotAllowAccessToUpdatePlans() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/cms/menus/update-plans", List.of());
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

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldDuplicate() throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        Long categoriesCount = categoryRepository.countByMenuId(1L);
        assertEquals("Całodniowe", existingMenu.name());
        assertTrue(existingMenu.standard());
        assertEquals(9L, categoriesCount);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate");

        Set<Menu> restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(2, restaurantMenus.size());
        Menu duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - kopia".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        User user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldDuplicateMultipleTimes() throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        Long categoriesCount = categoryRepository.countByMenuId(1L);
        assertEquals("Całodniowe", existingMenu.name());
        assertTrue(existingMenu.standard());
        assertEquals(9L, categoriesCount);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate");

        Set<Menu> restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(2, restaurantMenus.size());
        Menu duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - kopia".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        User user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate");

        restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(3, restaurantMenus.size());
        duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - kopia(1)".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate");

        restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(4, restaurantMenus.size());
        duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - kopia(2)".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldDuplicateWithEnLang() throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        Long categoriesCount = categoryRepository.countByMenuId(1L);
        assertEquals("Całodniowe", existingMenu.name());
        assertTrue(existingMenu.standard());
        assertEquals(9L, categoriesCount);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate", "en");

        Set<Menu> restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(2, restaurantMenus.size());
        Menu duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - copy".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        User user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotDuplicateWithUniqueNameViolation() throws Exception {
        MenuSimpleDTO existingMenu = apiRequestUtils.postObjectExpect200(
                "/api/cms/menus/show", 1, MenuSimpleDTO.class);
        Long categoriesCount = categoryRepository.countByMenuId(1L);
        assertEquals("Całodniowe", existingMenu.name());
        assertTrue(existingMenu.standard());
        assertEquals(9L, categoriesCount);

        apiRequestUtils.patchAndExpect200("/api/cms/menus/duplicate");

        Set<Menu> restaurantMenus = menuRepository.findAllByRestaurantId(1L);
        assertEquals(2, restaurantMenus.size());
        Menu duplicatedMenu = restaurantMenus.stream()
                .filter(m -> "Całodniowe - kopia".equals(m.getName()))
                .findFirst()
                .orElseThrow();
        assertFalse(duplicatedMenu.isStandard());
        assertEquals(9, duplicatedMenu.getCategories().size());

        User user = userRepository.findUserByUsername("admin@example.com");
        assertEquals(duplicatedMenu.getId(), user.getActiveMenuId());

        user.setActiveMenuId(1L);
        userRepository.saveAndFlush(user);
        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/menus/duplicate");
        assertEquals(1, errors.size());
        assertEquals("Nazwy menu powinny być unikalne.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "ff3abf8-9b6a@temp.it")
    void shouldNotAllowUnauthorizedDuplication() throws Exception {
        apiRequestUtils.patchAndExpect("/api/cms/menus/duplicate", status().isForbidden(), "pl");
    }

    private MenuSimpleDTO createMenuDTO(String name, Long restaurantId) {
        return new MenuSimpleDTO(null, restaurantId, name, getMenuColorDTO(), null, false);
    }

    private Set<MenuPlan> getPlan(Menu menu, Set<TimeRange> timeRanges) {
        Set<MenuPlan> plans = new HashSet<>();
        Arrays.asList(DayOfWeek.values()).forEach(dayOfWeek -> {
            MenuPlan plan = getMenuPlan(menu, timeRanges, dayOfWeek);
            plans.add(plan);
        });
        return plans;
    }

    private MenuPlan getMenuPlan(Menu menu, Set<TimeRange> timeRanges, DayOfWeek dayOfWeek) {
        MenuPlan menuPlan = new MenuPlan();
        menuPlan.setMenu(menu);
        menuPlan.setTimeRanges(timeRanges);
        menuPlan.setDayOfWeek(dayOfWeek);
        return menuPlan;
    }

    private Menu getMenu(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow();
        entityManager.detach(menu);
        return menu;
    }

    private MenuColorDTO getMenuColorDTO() {
        MenuColor menuColor = menuColorRepository.findById(9L).orElseThrow();
        return menuColorMapper.toDTO(menuColor);
    }
}