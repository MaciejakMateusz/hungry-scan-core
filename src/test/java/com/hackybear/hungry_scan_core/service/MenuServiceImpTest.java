package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuDeepCopyMapper;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.utility.Money;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImpTest implements WithAssertions {

    @Mock
    ExceptionHelper exceptionHelper;

    @Mock
    MenuRepository menuRepository;

    @Mock
    MenuMapper menuMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    MenuDeepCopyMapper menuDeepCopyMapper;

    @Mock
    S3Service s3Service;

    @InjectMocks
    MenuServiceImp service;

    Restaurant restaurant;
    User user;
    Menu menu;
    MenuSimpleDTO menuDto;
    MenuColorDTO color;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(11L);

        user = new User();
        user.setActiveRestaurantId(restaurant.getId());

        menu = new Menu();
        menu.setId(33L);
        menu.setName("Breakfast");
        menu.setRestaurant(restaurant);
        menu.setTheme(Theme.COLOR_318E41);
        menu.setCategories(getCategories());

        color = mock(MenuColorDTO.class);
        menuDto = new MenuSimpleDTO(
                menu.getId(),
                restaurant.getId(),
                menu.getName(),
                color,
                Collections.emptySet(),
                false);

        lenient().when(menuRepository.findById(menu.getId()))
                .thenReturn(Optional.of(menu));
        lenient().when(menuMapper.toSimpleDTO(menu))
                .thenReturn(menuDto);
    }

    @Test
    void shouldReturnMappedDTOs() throws LocalizedException {
        when(menuRepository.findAllByRestaurantId(restaurant.getId()))
                .thenReturn(Set.of(menu));

        Set<MenuSimpleDTO> result = service.findAll(restaurant.getId());

        assertThat(result).containsExactly(menuDto);
    }

    @Test
    void findByIdForWrongRestaurant() throws LocalizedException {
        LocalizedException ex = new LocalizedException("unauthorized");
        doThrow(ex).when(exceptionHelper)
                .throwLocalizedMessage("error.general.unauthorizedOperation");

        assertThatThrownBy(() -> service.findById(menu.getId(), 99L))
                .isSameAs(ex);
    }

    @Test
    void saveHappyPath() throws Exception {
        MenuSimpleDTO req = new MenuSimpleDTO(
                null,
                restaurant.getId(),
                "Lunch",
                color,
                Collections.emptySet(),
                false);

        when(menuRepository.existsByRestaurantIdAndName(restaurant.getId(), "Lunch"))
                .thenReturn(false);
        when(restaurantRepository.findById(restaurant.getId()))
                .thenReturn(Optional.of(restaurant));

        Menu mapped = new Menu();
        mapped.setName("Lunch");
        when(menuMapper.toMenu(req)).thenReturn(mapped);

        Menu saved = new Menu();
        saved.setId(55L);
        when(menuRepository.save(any(Menu.class))).thenReturn(saved);

        service.save(req, user);

        verify(menuRepository).save(any(Menu.class));
        verify(userRepository).save(user);
        assertThat(user.getActiveMenuId()).isEqualTo(55L);
    }

    @Test
    void saveDuplicateName() throws Exception {
        MenuSimpleDTO req = new MenuSimpleDTO(
                null,
                restaurant.getId(),
                "Breakfast",
                color,
                Collections.emptySet(),
                false);

        when(menuRepository.existsByRestaurantIdAndName(restaurant.getId(), "Breakfast"))
                .thenReturn(true);

        LocalizedException duplicate = new LocalizedException("duplicate");
        doThrow(duplicate).when(exceptionHelper)
                .throwLocalizedMessage("error.menuService.uniqueNameViolation");

        assertThatThrownBy(() -> service.save(req, user)).isSameAs(duplicate);
        verify(menuRepository, never()).save(any());
    }

    @Test
    void updateMenu() throws Exception {
        MenuSimpleDTO req = new MenuSimpleDTO(
                menu.getId(),
                restaurant.getId(),
                "Brunch-Renamed",
                color,
                Collections.emptySet(),
                false);

        when(menuMapper.toMenu(req)).thenAnswer(inv -> {
            Menu m = new Menu();
            m.setName("Brunch-Renamed");
            return m;
        });

        when(menuRepository.existsByRestaurantIdAndName(restaurant.getId(), "Brunch-Renamed"))
                .thenReturn(false);

        service.update(req, restaurant.getId());

        verify(menuRepository).saveAndFlush(menu);
        assertThat(menu.getName()).isEqualTo("Brunch-Renamed");
    }

    @Test
    void deleteStandardMenu() throws LocalizedException {
        menu.setStandard(true);
        user.setActiveMenuId(menu.getId());

        LocalizedException ex = new LocalizedException("illegal");
        doThrow(ex).when(exceptionHelper)
                .throwLocalizedMessage("error.menuService.illegalMenuRemoval");

        assertThatThrownBy(() -> service.delete(user)).isSameAs(ex);
        verify(menuRepository, never()).delete(any());
    }

    @Test
    void deleteNonStandard() throws LocalizedException {
        menu.setStandard(false);
        user.setActiveMenuId(menu.getId());

        when(menuRepository.findStandardIdByRestaurantId(restaurant.getId()))
                .thenReturn(Optional.of(77L));

        Category cat = new Category();
        MenuItem item = new MenuItem();
        item.setId(200L);
        cat.setMenuItems(Set.of(item));
        menu.setCategories(Set.of(cat));

        service.delete(user);

        verify(menuRepository).delete(menu);
        verify(userRepository).save(user);
        verify(s3Service).deleteAllFiles("menuItems", List.of(200L));
        assertThat(user.getActiveMenuId()).isEqualTo(77L);
    }

    @Test
    void duplicateCreatesCopy() throws LocalizedException {
        user.setActiveMenuId(menu.getId());

        Menu copied = new Menu();
        copied.setId(101L);
        copied.setName("Breakfast (copy)");
        copied.setCategories(Collections.emptySet());

        when(menuRepository.findByIdWithAllGraph(menu.getId()))
                .thenReturn(Optional.of(menu));
        when(menuDeepCopyMapper.duplicateMenu(menu))
                .thenReturn(copied);

        when(menuRepository.existsByRestaurantIdAndName(eq(restaurant.getId()), anyString()))
                .thenReturn(false);

        when(menuRepository.save(copied)).thenReturn(copied);

        service.duplicate(user);

        verify(menuRepository).save(copied);
        verify(userRepository).save(user);
        assertThat(user.getActiveMenuId()).isEqualTo(101L);
    }

    @Test
    void duplicateCreatesCopyWithCategories() throws LocalizedException {
        user.setActiveMenuId(menu.getId());

        Menu copied = new Menu();
        copied.setId(101L);
        copied.setName("Breakfast (copy)");

        Variant v = new Variant();
        MenuItem mi = new MenuItem();
        mi.setVariants(Set.of(v));
        Category cat = new Category();
        cat.setMenuItems(Set.of(mi));
        copied.setCategories(Set.of(cat));

        when(menuRepository.findByIdWithAllGraph(menu.getId()))
                .thenReturn(Optional.of(menu));
        when(menuDeepCopyMapper.duplicateMenu(menu))
                .thenReturn(copied);
        when(menuRepository.existsByRestaurantIdAndName(eq(restaurant.getId()), anyString()))
                .thenReturn(false);
        when(menuRepository.save(copied)).thenReturn(copied);

        service.duplicate(user);

        verify(menuRepository).save(copied);
        verify(userRepository).save(user);
        assertThat(user.getActiveMenuId()).isEqualTo(101L);

        assertThat(cat.getMenu()).isSameAs(copied);
        assertThat(mi.getCategory()).isSameAs(cat);
        assertThat(v.getMenuItem()).isSameAs(mi);
    }

    @Test
    void findByIdReturnsDto() throws LocalizedException {
        when(menuMapper.toSimpleDTO(menu)).thenReturn(menuDto);
        MenuSimpleDTO dto = service.findById(menu.getId(), restaurant.getId());
        assertThat(dto).isEqualTo(menuDto);
        verify(menuMapper).toSimpleDTO(menu);
    }

    @Test
    void projectPlannedMenuReturnsCustomerDto() throws LocalizedException {
        MenuCustomerDTO customerDto = mock(MenuCustomerDTO.class);
        when(menuMapper.toCustomerDTO(menu)).thenReturn(customerDto);
        MenuCustomerDTO result = service.projectPlannedMenu(menu.getId());

        assertThat(result).isSameAs(customerDto);
        verify(menuMapper).toCustomerDTO(menu);
    }

    @Test
    void switchStandardResetsPreviousAndMarksNewOne() {
        user.setActiveRestaurantId(restaurant.getId());
        user.setActiveMenuId(menu.getId());

        service.switchStandard(user);

        InOrder order = inOrder(menuRepository);
        order.verify(menuRepository).resetStandardMenus(restaurant.getId());
        order.verify(menuRepository).switchStandard(menu.getId());
        order.verifyNoMoreInteractions();
    }

    private Set<Category> getCategories() {
        Category cat1 = new Category();
        cat1.setId(100L);
        cat1.setMenuItems(getMenuItems(100L));
        cat1.setName(new Translatable().withPl("One hundred"));
        cat1.setMenu(menu);

        Category cat2 = new Category();
        cat2.setId(200L);
        cat2.setMenuItems(getMenuItems(200L));
        cat2.setName(new Translatable().withPl("Two hundred"));
        cat2.setMenu(menu);

        Set<Category> categories = new HashSet<>();
        categories.add(cat1);
        categories.add(cat2);
        return categories;
    }

    private Set<MenuItem> getMenuItems(Long categoryId) {
        MenuItem item1 = new MenuItem();
        item1.setId(categoryId + 5);
        item1.setCategory(new Category(categoryId));
        item1.setName(new Translatable().withPl("Five five five"));
        item1.setPrice(Money.of(20));

        MenuItem item2 = new MenuItem();
        item2.setId(categoryId + 6);
        item2.setCategory(new Category(categoryId));
        item2.setName(new Translatable().withPl("Six six six"));
        item2.setPrice(Money.of(40));

        Set<MenuItem> menuItems = new HashSet<>();
        menuItems.add(item1);
        menuItems.add(item2);
        return menuItems;
    }
}