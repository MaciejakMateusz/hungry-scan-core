//package com.hackybear.hungry_scan_core.service;
//
//import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
//import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
//import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
//import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
//import com.hackybear.hungry_scan_core.dto.mapper.MenuDeepCopyMapper;
//import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
//import com.hackybear.hungry_scan_core.entity.*;
//import com.hackybear.hungry_scan_core.enums.Theme;
//import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
//import com.hackybear.hungry_scan_core.exception.LocalizedException;
//import com.hackybear.hungry_scan_core.repository.MenuRepository;
//import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
//import com.hackybear.hungry_scan_core.repository.UserRepository;
//import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
//import com.hackybear.hungry_scan_core.utility.StandardDayPlanScheduler;
//import com.hackybear.hungry_scan_core.utility.TimeRange;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.*;
//import java.util.function.Supplier;
//
//import static com.hackybear.hungry_scan_core.service.MenuServiceImp.S3_PATH;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MenuServiceImpTest {
//
//    @Mock
//    ExceptionHelper exceptionHelper;
//    @Mock
//    MenuRepository menuRepository;
//    @Mock
//    MenuMapper menuMapper;
//    @Mock
//    UserRepository userRepository;
//    @Mock
//    RestaurantRepository restaurantRepository;
//    @Mock
//    MenuDeepCopyMapper menuDeepCopyMapper;
//    @Mock
//    StandardDayPlanScheduler standardDayPlanScheduler;
//    @Mock
//    S3Service s3Service;
//
//    @InjectMocks
//    private MenuServiceImp menuService;
//
//    private User currentUser;
//    private Restaurant restaurant;
//
//    @BeforeEach
//    void setUp() {
//        currentUser = new User();
//        currentUser.setId(42L);
//        currentUser.setActiveRestaurantId(100L);
//
//        restaurant = new Restaurant();
//        restaurant.setId(100L);
//    }
//
//    @Test
//    void findAll_returnsMappedSortedSimpleDTOs() throws LocalizedException {
//        Menu m1 = new Menu();
//        m1.setId(1L);
//        m1.setName("B");
//        Menu m2 = new Menu();
//        m2.setId(2L);
//        m2.setName("A");
//
//        when(menuRepository.findAllByRestaurantId(100L))
//                .thenReturn(Set.of(m1, m2));
//
//        MenuSimpleDTO dto1 = new MenuSimpleDTO(2L, 100L, "A", new HashSet<>(), new ArrayList<>(), true);
//        MenuSimpleDTO dto2 = new MenuSimpleDTO(1L, 100L, "B", new HashSet<>(), new ArrayList<>(), false);
//
//        when(menuMapper.toSimpleDTO(m2)).thenReturn(dto1);
//        when(menuMapper.toSimpleDTO(m1)).thenReturn(dto2);
//
//        Set<MenuSimpleDTO> result = menuService.findAll(100L);
//
//        Iterator<MenuSimpleDTO> it = result.iterator();
//        assertEquals("A", it.next().name());
//        assertEquals("B", it.next().name());
//        verify(menuRepository).findAllByRestaurantId(100L);
//    }
//
//    @Test
//    void findById_whenOwner_returnsDTO() throws LocalizedException {
//        Menu menu = new Menu();
//        menu.setId(5L);
//        menu.setRestaurant(restaurant);
//
//        when(menuRepository.findById(5L)).thenReturn(Optional.of(menu));
//        MenuSimpleDTO dto = new MenuSimpleDTO(5L, 100L, "X", new HashSet<>(), new ArrayList<>(), true);
//        when(menuMapper.toSimpleDTO(menu)).thenReturn(dto);
//
//        MenuSimpleDTO out = menuService.findById(5L, 100L);
//        assertSame(dto, out);
//        verify(menuRepository).findById(5L);
//    }
//
//    @Test
//    void findById_whenNotOwner_throwsException() throws LocalizedException {
//        Menu menu = new Menu();
//        menu.setId(5L);
//        Restaurant other = new Restaurant();
//        other.setId(999L);
//        menu.setRestaurant(other);
//
//        when(menuRepository.findById(5L)).thenReturn(Optional.of(menu));
//        doThrow(new LocalizedException("unauth"))
//                .when(exceptionHelper).throwLocalizedMessage("error.general.unauthorizedOperation");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.findById(5L, 100L));
//    }
//
//    @Test
//    void projectPlannedMenu_mapsToCustomerDTO() throws LocalizedException {
//        Menu menu = new Menu();
//        menu.setId(7L);
//        when(menuRepository.findById(7L)).thenReturn(Optional.of(menu));
//        MenuCustomerDTO cust = new MenuCustomerDTO(
//                new ArrayList<>(),
//                new RestaurantSimpleDTO(100L, "R"),
//                "black",
//                new TranslatableDTO(55L, "D", null, null, null, null, null));
//        when(menuMapper.toCustomerDTO(menu)).thenReturn(cust);
//
//        MenuCustomerDTO out = menuService.projectPlannedMenu(7L);
//        assertSame(cust, out);
//    }
//
//    @Test
//    void save_happyPath_persistsMenuAndUpdatesUser() throws Exception {
//        MenuSimpleDTO dto = new MenuSimpleDTO(null, 100L, "New", new HashSet<>(), new ArrayList<>(), false);
//        when(menuRepository.existsByRestaurantIdAndName(100L, "New")).thenReturn(false);
//
//        Menu toSave = new Menu();
//        when(menuMapper.toMenu(dto)).thenReturn(toSave);
//        when(restaurantRepository.findById(100L)).thenReturn(Optional.of(restaurant));
//
//        Menu saved = new Menu();
//        saved.setId(55L);
//        when(menuRepository.save(toSave)).thenReturn(saved);
//
//        menuService.save(dto, currentUser);
//
//        assertEquals(55L, currentUser.getActiveMenuId());
//        verify(menuRepository).save(toSave);
//        verify(userRepository).save(currentUser);
//    }
//
//    @Test
//    void save_whenNameExists_throws() throws LocalizedException {
//        MenuSimpleDTO dto = new MenuSimpleDTO(null, 100L, "Dup", new HashSet<>(), new ArrayList<>(), false);
//        when(menuRepository.existsByRestaurantIdAndName(100L, "Dup")).thenReturn(true);
//        doThrow(new LocalizedException("dup"))
//                .when(exceptionHelper).throwLocalizedMessage("error.menuService.uniqueNameViolation");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.save(dto, currentUser));
//    }
//
//    @Test
//    void update_whenNameChangedAndUnique_savesAndFlushes() throws Exception {
//        MenuSimpleDTO dto = new MenuSimpleDTO(9L, 100L, "Renamed", new HashSet<>(), new ArrayList<>(), false);
//        Menu menu = new Menu();
//        menu.setId(9L);
//        menu.setName("Old");
//        menu.setRestaurant(restaurant);
//
//        when(menuRepository.findById(9L)).thenReturn(Optional.of(menu));
//        when(menuRepository.existsByRestaurantIdAndName(100L, "Renamed")).thenReturn(false);
//
//        menuService.update(dto, 100L);
//
//        assertEquals("Renamed", menu.getName());
//        verify(menuRepository).saveAndFlush(menu);
//    }
//
//    @Test
//    void update_whenNameCollides_throws() throws LocalizedException {
//        MenuSimpleDTO dto = new MenuSimpleDTO(9L, 100L, "Rename", new HashSet<>(), new ArrayList<>(), false);
//        Menu menu = new Menu();
//        menu.setId(9L);
//        menu.setName("Old");
//        menu.setRestaurant(restaurant);
//
//        when(menuRepository.findById(9L)).thenReturn(Optional.of(menu));
//        when(menuRepository.existsByRestaurantIdAndName(100L, "Rename")).thenReturn(true);
//        doThrow(new LocalizedException("dup"))
//                .when(exceptionHelper).throwLocalizedMessage("error.menuService.uniqueNameViolation");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.update(dto, 100L));
//    }
//
//    @Test
//    void updatePlans_whenSchedulesOverlap_throws() throws LocalizedException {
//        TimeRange r1 = new TimeRange(LocalTime.of(8, 0), LocalTime.of(10, 0));
//        TimeRange r2 = new TimeRange(LocalTime.of(9, 0), LocalTime.of(11, 0));
//
//        MenuSimpleDTO m1 = new MenuSimpleDTO(1L, 100L, "A", Map.of(DayOfWeek.MONDAY, r1), new ArrayList<>(), false);
//        MenuSimpleDTO m2 = new MenuSimpleDTO(2L, 100L, "B", Map.of(DayOfWeek.MONDAY, r2), new ArrayList<>(), false);
//
//        doThrow(new LocalizedException("collide"))
//                .when(exceptionHelper).throwLocalizedMessage("error.menuService.schedulesCollide");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.updatePlans(List.of(m1, m2), 100L));
//    }
//
//    @Test
//    void updatePlans_nonOverlapping_updatesAndSchedules() throws LocalizedException {
//        TimeRange r1 = new TimeRange(LocalTime.of(8, 0), LocalTime.of(9, 0));
//        TimeRange r2 = new TimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0));
//
//        MenuSimpleDTO m1 = new MenuSimpleDTO(1L, 100L, "A", Map.of(DayOfWeek.TUESDAY, r1), new ArrayList<>(), false);
//        MenuSimpleDTO m2 = new MenuSimpleDTO(2L, 100L, "B", Map.of(DayOfWeek.TUESDAY, r2), new ArrayList<>(), false);
//
//        Menu menu1 = new Menu();
//        menu1.setId(1L);
//        menu1.setPlan(Map.of(DayOfWeek.TUESDAY, r1));
//        Menu menu2 = new Menu();
//        menu2.setId(2L);
//        menu2.setPlan(Map.of(DayOfWeek.TUESDAY, new TimeRange(LocalTime.of(9, 0), LocalTime.of(10, 0))));
//        when(menuRepository.findAllByRestaurantId(100L)).thenReturn(Set.of(menu1, menu2));
//
//        menuService.updatePlans(List.of(m1, m2), 100L);
//
//        @SuppressWarnings("unchecked")
//        ArgumentCaptor<List<Menu>> captor = ArgumentCaptor.forClass(List.class);
//        verify(menuRepository).saveAll(captor.capture());
//        List<Menu> saved = captor.getValue();
//        assertEquals(1, saved.size());
//        assertEquals(r2, saved.getFirst().getPlan().get(DayOfWeek.TUESDAY));
//        verify(standardDayPlanScheduler).mapStandardPlan(anyList());
//    }
//
//    @Test
//    void switchStandard_invokesResetSwitchAndScheduler() throws LocalizedException {
//        currentUser.setActiveMenuId(7L);
//
//        doNothing().when(menuRepository).resetStandardMenus(100L);
//        doNothing().when(menuRepository).switchStandard(7L);
//
//        Menu m = new Menu();
//        m.setId(7L);
//        m.setRestaurant(restaurant);
//        when(menuRepository.findById(7L)).thenReturn(Optional.of(m));
//        when(menuRepository.findAllByRestaurantId(100L)).thenReturn(Set.of(m));
//
//        menuService.switchStandard(currentUser);
//
//        verify(menuRepository).resetStandardMenus(100L);
//        verify(menuRepository).switchStandard(7L);
//        verify(menuRepository).saveAndFlush(m);
//        verify(standardDayPlanScheduler).mapStandardPlan(anyList());
//    }
//
//    @Test
//    void delete_whenStandard_throws() throws LocalizedException {
//        Menu std = new Menu();
//        std.setId(8L);
//        std.setRestaurant(restaurant);
//        std.setStandard(true);
//        currentUser.setActiveMenuId(8L);
//
//        when(menuRepository.findById(8L)).thenReturn(Optional.of(std));
//        doThrow(new LocalizedException("dup"))
//                .when(exceptionHelper)
//                .throwLocalizedMessage("error.menuService.illegalMenuRemoval");
//
//        assertThrows(LocalizedException.class, () -> menuService.delete(currentUser));
//    }
//
//    @Test
//    void delete_nonStandard_deletesAndCleansUp() throws LocalizedException {
//        Menu ex = new Menu();
//        ex.setId(9L);
//        ex.setRestaurant(restaurant);
//        ex.setStandard(false);
//        Category cat = new Category();
//        cat.setMenu(ex);
//        MenuItem mi1 = new MenuItem();
//        mi1.setId(101L);
//        mi1.setCategory(cat);
//        cat.setMenuItems(Set.of(mi1));
//        ex.setCategories(Set.of(cat));
//
//        currentUser.setActiveMenuId(9L);
//        when(menuRepository.findById(9L)).thenReturn(Optional.of(ex));
//        when(menuRepository.findStandardIdByRestaurantId(100L)).thenReturn(Optional.of(1L));
//
//        menuService.delete(currentUser);
//
//        verify(menuRepository).delete(ex);
//        verify(userRepository).save(currentUser);
//        verify(s3Service).deleteAllFiles(eq(S3_PATH), eq(List.of(101L)));
//        assertEquals(1L, currentUser.getActiveMenuId());
//    }
//
//    @Test
//    void duplicate_happyPath_createsCopyAndSavesUser() throws LocalizedException {
//        Menu src = new Menu();
//        src.setId(20L);
//        src.setRestaurant(restaurant);
//        src.setName("Orig");
//        Category cat = new Category();
//        cat.setMenu(src);
//        MenuItem mi = new MenuItem();
//        mi.setCategory(cat);
//        cat.setMenuItems(Set.of(mi));
//        src.setCategories(Set.of(cat));
//
//        currentUser.setActiveMenuId(20L);
//        when(menuRepository.findByIdWithAllGraph(20L)).thenReturn(Optional.of(src));
//
//        Menu copy = new Menu();
//        when(menuDeepCopyMapper.duplicateMenu(src)).thenReturn(copy);
//
//        when(menuRepository.existsByRestaurantIdAndName(eq(100L), anyString())).thenReturn(false);
//        when(menuRepository.save(copy)).then(invocation -> {
//            copy.setId(99L);
//            return copy;
//        });
//
//        menuService.duplicate(currentUser);
//
//        assertEquals(99L, currentUser.getActiveMenuId());
//        verify(userRepository).save(currentUser);
//    }
//
//    @Test
//    void duplicate_whenNameCollides_throws() throws LocalizedException {
//        long srcId = 30L;
//        currentUser.setActiveMenuId(srcId);
//
//        Menu src = new Menu();
//        src.setId(srcId);
//        src.setRestaurant(restaurant);
//        src.setName("Source menu");
//        when(menuRepository.findByIdWithAllGraph(srcId))
//                .thenReturn(Optional.of(src));
//
//        Menu dup = new Menu();
//        when(menuDeepCopyMapper.duplicateMenu(src))
//                .thenReturn(dup);
//
//        when(menuRepository.existsByRestaurantIdAndName(eq(100L), anyString()))
//                .thenReturn(true);
//        doThrow(new LocalizedException("dup"))
//                .when(exceptionHelper)
//                .throwLocalizedMessage("error.menuService.uniqueNameViolation");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.duplicate(currentUser));
//
//        verify(menuRepository).findByIdWithAllGraph(srcId);
//    }
//
//    @Test
//    void findById_whenMenuNotFound_throws() {
//        when(menuRepository.findById(123L)).thenReturn(Optional.empty());
//        Supplier<LocalizedException> supplier = () -> new LocalizedException("notfound");
//        when(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"))
//                .thenReturn(supplier);
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.findById(123L, currentUser.getActiveRestaurantId()));
//    }
//
//    @Test
//    void projectPlannedMenu_whenMenuNotFound_throws() {
//        when(menuRepository.findById(77L)).thenReturn(Optional.empty());
//        Supplier<LocalizedException> supplier = () -> new LocalizedException("notfound");
//        when(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"))
//                .thenReturn(supplier);
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.projectPlannedMenu(77L));
//    }
//
//    @Test
//    void save_setsDefaultThemeAndMessage() throws Exception {
//        MenuSimpleDTO dto = new MenuSimpleDTO(null, 100L, "MyMenu", Map.of(), List.of(), false);
//        when(menuRepository.existsByRestaurantIdAndName(100L, "MyMenu")).thenReturn(false);
//        Menu toSave = new Menu();
//        when(menuMapper.toMenu(dto)).thenReturn(toSave);
//        when(restaurantRepository.findById(100L)).thenReturn(Optional.of(restaurant));
//        Menu saved = new Menu();
//        saved.setId(200L);
//        when(menuRepository.save(toSave)).thenReturn(saved);
//
//        menuService.save(dto, currentUser);
//
//        assertEquals(Theme.COLOR_318E41, toSave.getTheme(), "should set default theme");
//        assertNotNull(toSave.getMessage(), "should set default message");
//        assertEquals("Welcome!", toSave.getMessage().getPl(),
//                "default translation should be 'Welcome!'");
//    }
//
//    @Test
//    void update_whenMenuNotFound_throws() {
//        MenuSimpleDTO dto = new MenuSimpleDTO(5L, 100L, "NewName", Map.of(), List.of(), false);
//        when(menuRepository.findById(5L)).thenReturn(Optional.empty());
//        Supplier<LocalizedException> supplier = () -> new LocalizedException("notfound");
//        when(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"))
//                .thenReturn(supplier);
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.update(dto, currentUser.getActiveRestaurantId()));
//    }
//
//    @Test
//    void delete_whenMenuNotFound_throws() {
//        currentUser.setActiveMenuId(500L);
//        when(menuRepository.findById(500L)).thenReturn(Optional.empty());
//        Supplier<LocalizedException> supplier = () -> new LocalizedException("notfound");
//        when(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"))
//                .thenReturn(supplier);
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.delete(currentUser));
//    }
//
//    @Test
//    void delete_whenUnauthorized_throws() throws LocalizedException {
//        Menu otherMenu = new Menu();
//        otherMenu.setId(6L);
//        Restaurant otherRest = new Restaurant();
//        otherRest.setId(999L);
//        otherMenu.setRestaurant(otherRest);
//        currentUser.setActiveMenuId(6L);
//        when(menuRepository.findById(6L)).thenReturn(Optional.of(otherMenu));
//        doThrow(new LocalizedException("unauth"))
//                .when(exceptionHelper).throwLocalizedMessage("error.general.unauthorizedOperation");
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.delete(currentUser));
//    }
//
//    @Test
//    void updatePlans_withNullPlan_clearsPlanAndSchedules() throws LocalizedException {
//        MenuSimpleDTO dto = new MenuSimpleDTO(
//                1L,
//                currentUser.getActiveRestaurantId(),
//                "A",
//                new HashSet<>(),
//                List.of(),
//                false
//        );
//
//        TimeRange oldRange = new TimeRange(
//                LocalTime.of(8, 0),
//                LocalTime.of(9, 0)
//        );
//        Menu existing = new Menu();
//        existing.setId(1L);
//        existing.setPlan(Map.of(DayOfWeek.WEDNESDAY, oldRange));
//
//        when(menuRepository.findAllByRestaurantId(currentUser.getActiveRestaurantId()))
//                .thenReturn(Set.of(existing));
//
//        menuService.updatePlans(List.of(dto), currentUser.getActiveRestaurantId());
//
//        @SuppressWarnings("unchecked")
//        ArgumentCaptor<List<Menu>> captor = ArgumentCaptor.forClass(List.class);
//        verify(menuRepository).saveAll(captor.capture());
//        List<Menu> saved = captor.getValue();
//
//        assertEquals(1, saved.size(), "only the one changed menu should be saved");
//        assertEquals(new HashMap<>(), saved.getFirst().getPlan(), "the menu’s plan should have been emptied");
//
//        verify(standardDayPlanScheduler).mapStandardPlan(List.of(dto));
//    }
//
//
//    @Test
//    void updatePlans_withStandardDTO_updatesPlan() throws LocalizedException {
//        TimeRange oldRange = new TimeRange(LocalTime.of(11, 0), LocalTime.of(12, 0));
//        TimeRange newRange = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
//        MenuSimpleDTO dto = new MenuSimpleDTO(2L, 100L, "Std", Map.of(DayOfWeek.THURSDAY, newRange), List.of(), true);
//        Menu existing = new Menu();
//        existing.setId(2L);
//        existing.setPlan(Map.of(DayOfWeek.THURSDAY, oldRange));
//        when(menuRepository.findAllByRestaurantId(100L)).thenReturn(Set.of(existing));
//
//        menuService.updatePlans(List.of(dto), currentUser.getActiveRestaurantId());
//
//        @SuppressWarnings("unchecked")
//        ArgumentCaptor<List<Menu>> captor = ArgumentCaptor.forClass(List.class);
//        verify(menuRepository).saveAll(captor.capture());
//        List<Menu> saved = captor.getValue();
//        assertEquals(1, saved.size());
//        assertEquals(newRange, saved.getFirst().getPlan().get(DayOfWeek.THURSDAY));
//        verify(standardDayPlanScheduler).mapStandardPlan(anyList());
//    }
//
//    @Test
//    void delete_multipleMenuItems_removesAllFiles() throws LocalizedException {
//        Menu ex = new Menu();
//        ex.setId(10L);
//        ex.setRestaurant(restaurant);
//        Category c1 = new Category();
//        c1.setMenu(ex);
//        MenuItem mi1 = new MenuItem();
//        mi1.setId(201L);
//        mi1.setCategory(c1);
//        c1.setMenuItems(Set.of(mi1));
//        Category c2 = new Category();
//        c2.setMenu(ex);
//        MenuItem mi2 = new MenuItem();
//        mi2.setId(202L);
//        mi2.setCategory(c2);
//        c2.setMenuItems(Set.of(mi2));
//        ex.setCategories(Set.of(c1, c2));
//        currentUser.setActiveMenuId(10L);
//
//        when(menuRepository.findById(10L)).thenReturn(Optional.of(ex));
//        when(menuRepository.findStandardIdByRestaurantId(100L)).thenReturn(Optional.of(1L));
//
//        menuService.delete(currentUser);
//
//        @SuppressWarnings("unchecked")
//        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
//        verify(s3Service).deleteAllFiles(eq(S3_PATH), captor.capture());
//        List<Long> ids = captor.getValue();
//        assertTrue(ids.containsAll(List.of(201L, 202L)),
//                "should delete files for all menu‐item IDs");
//    }
//
//    @Test
//    void switchStandard_resetsPlanOfActiveMenu() throws LocalizedException {
//        currentUser.setActiveMenuId(8L);
//        Menu m = new Menu();
//        m.setId(8L);
//        m.setRestaurant(restaurant);
//        m.setPlan(Map.of(DayOfWeek.MONDAY,
//                new TimeRange(LocalTime.of(7, 0), LocalTime.of(8, 0))));
//        when(menuRepository.findById(8L)).thenReturn(Optional.of(m));
//        when(menuRepository.findAllByRestaurantId(100L)).thenReturn(Set.of(m));
//        doNothing().when(menuRepository).resetStandardMenus(100L);
//        doNothing().when(menuRepository).switchStandard(8L);
//
//        menuService.switchStandard(currentUser);
//
//        assertTrue(m.getPlan().isEmpty(), "after switchStandard the chosen menu's plan should be cleared");
//        verify(menuRepository).saveAndFlush(m);
//    }
//
//    @Test
//    void duplicate_whenMenuNotFound_throws() {
//        currentUser.setActiveMenuId(300L);
//        when(menuRepository.findByIdWithAllGraph(300L)).thenReturn(Optional.empty());
//        Supplier<LocalizedException> supplier = () -> new LocalizedException("nf");
//        when(exceptionHelper.supplyLocalizedMessage("error.menuService.menuNotFound"))
//                .thenReturn(supplier);
//
//        assertThrows(LocalizedException.class,
//                () -> menuService.duplicate(currentUser));
//    }
//
//    @Test
//    void duplicate_deepCopy_relationshipsAreRewired() throws LocalizedException {
//        Menu src = new Menu();
//        src.setId(20L);
//        src.setRestaurant(restaurant);
//        src.setName("Source menu");
//        currentUser.setActiveMenuId(20L);
//        when(menuRepository.findByIdWithAllGraph(20L)).thenReturn(Optional.of(src));
//
//        Menu copy = new Menu();
//        Category catCopy = new Category();
//        MenuItem itemCopy = new MenuItem();
//        Variant varCopy = new Variant();
//        itemCopy.setVariants(Set.of(varCopy));
//        catCopy.setMenuItems(Set.of(itemCopy));
//        copy.setCategories(Set.of(catCopy));
//
//        when(menuDeepCopyMapper.duplicateMenu(src)).thenReturn(copy);
//        when(menuRepository.existsByRestaurantIdAndName(eq(100L), anyString())).thenReturn(false);
//        when(menuRepository.save(copy)).thenAnswer(inv -> {
//            copy.setId(99L);
//            return copy;
//        });
//
//        menuService.duplicate(currentUser);
//
//        for (Category c : copy.getCategories()) {
//            assertEquals(copy, c.getMenu());
//            for (MenuItem mi : c.getMenuItems()) {
//                assertEquals(c, mi.getCategory());
//                for (Variant v : mi.getVariants()) {
//                    assertEquals(mi, v.getMenuItem());
//                }
//            }
//        }
//        assertEquals(99L, currentUser.getActiveMenuId());
//    }
//}