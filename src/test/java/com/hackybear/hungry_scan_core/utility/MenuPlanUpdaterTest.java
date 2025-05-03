package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class MenuPlanUpdaterTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private StandardDayPlanScheduler scheduler;

    @InjectMocks
    private MenuPlanUpdater updater;

    private static Restaurant mockRestaurant(Long id,
                                             LocalTime opening,
                                             LocalTime closing) {

        Restaurant r = mock(Restaurant.class, Answers.RETURNS_DEEP_STUBS);
        when(r.getId()).thenReturn(id);
        when(r.getSettings().getOpeningTime()).thenReturn(opening);
        when(r.getSettings().getClosingTime()).thenReturn(closing);
        return r;
    }

    private static Menu buildMenu(Restaurant r,
                                  LocalTime from,
                                  LocalTime to) {

        Menu m = new Menu();
        m.setId(11L);
        m.setName("Breakfast");
        m.setRestaurant(r);
        m.setStandard(false);

        Map<DayOfWeek, TimeRange> plan = new EnumMap<>(DayOfWeek.class);
        plan.put(DayOfWeek.MONDAY, new TimeRange(from, to));
        m.setPlan(plan);
        return m;
    }

    private void stubMapper(Menu menu) {
        when(menuMapper.toSimpleDTO(menu))
                .then(invocation -> new MenuSimpleDTO(
                        menu.getId(),
                        menu.getRestaurant().getId(),
                        menu.getName(),
                        menu.getPlan(),
                        List.of(),
                        false));
    }


    @Test
    @SuppressWarnings("unchecked")
    void updateMenusPlans_shiftsOpening_startTrimmed() throws Exception {
        Restaurant oldRest = mockRestaurant(1L, LocalTime.of(8, 0), LocalTime.of(22, 0));
        RestaurantDTO newDto = getRestaurantDTO(LocalTime.of(9, 0), LocalTime.of(22, 0));

        Menu menu = buildMenu(oldRest, LocalTime.of(8, 0), LocalTime.of(10, 0));
        stubMapper(menu);

        when(menuRepository.findAllByRestaurantId(1L)).thenReturn(Set.of(menu));

        updater.updateMenusPlans(oldRest, newDto);

        ArgumentCaptor<Set<Menu>> captor = ArgumentCaptor.forClass(Set.class);
        verify(menuRepository).saveAll(captor.capture());

        Map<DayOfWeek, TimeRange> updated =
                captor.getValue().iterator().next().getPlan();

        TimeRange slot = updated.get(DayOfWeek.MONDAY);
        assertThat(slot).isNotNull();
        assertThat(slot.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(slot.getEndTime()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateMenusPlans_shiftsClosing_endTrimmed() throws Exception {
        Restaurant oldRest = mockRestaurant(2L, LocalTime.of(8, 0), LocalTime.of(22, 0));
        RestaurantDTO newDto = getRestaurantDTO(LocalTime.of(8, 0), LocalTime.of(20, 0));

        Menu menu = buildMenu(oldRest, LocalTime.of(19, 0), LocalTime.of(21, 0));
        stubMapper(menu);

        when(menuRepository.findAllByRestaurantId(2L)).thenReturn(Set.of(menu));

        updater.updateMenusPlans(oldRest, newDto);


        ArgumentCaptor<Set<Menu>> captor = ArgumentCaptor.forClass(Set.class);
        verify(menuRepository).saveAll(captor.capture());

        Map<DayOfWeek, TimeRange> updated =
                captor.getValue().iterator().next().getPlan();

        TimeRange slot = updated.get(DayOfWeek.MONDAY);
        assertThat(slot).isNotNull();
        assertThat(slot.getStartTime()).isEqualTo(LocalTime.of(19, 0));
        assertThat(slot.getEndTime()).isEqualTo(LocalTime.of(20, 0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateMenusPlans_openingPastEntireSlot_planCleared() throws Exception {
        Restaurant oldRest = mockRestaurant(3L, LocalTime.of(8, 0), LocalTime.of(22, 0));
        RestaurantDTO newDto = getRestaurantDTO(LocalTime.of(10, 0), LocalTime.of(22, 0));

        Menu menu = buildMenu(oldRest, LocalTime.of(8, 0), LocalTime.of(9, 0));
        stubMapper(menu);

        when(menuRepository.findAllByRestaurantId(3L)).thenReturn(Set.of(menu));

        updater.updateMenusPlans(oldRest, newDto);

        ArgumentCaptor<Set<Menu>> captor = ArgumentCaptor.forClass(Set.class);
        verify(menuRepository).saveAll(captor.capture());

        Map<DayOfWeek, TimeRange> updated =
                captor.getValue().iterator().next().getPlan();

        assertThat(updated).isEmpty();
    }

    @Test
    void updateMenusPlans_openingUnchanged_noOp() throws Exception {
        Restaurant oldRest = mockRestaurant(4L, LocalTime.of(8, 0), LocalTime.of(22, 0));
        RestaurantDTO sameDto = getRestaurantDTO(LocalTime.of(8, 0), LocalTime.of(22, 0));

        updater.updateMenusPlans(oldRest, sameDto);

        verifyNoInteractions(menuRepository, menuMapper, scheduler);
    }

    private RestaurantDTO getRestaurantDTO(LocalTime opening, LocalTime closing) {
        com.hackybear.hungry_scan_core.dto.SettingsDTO settings =
                Mockito.mock(com.hackybear.hungry_scan_core.dto.SettingsDTO.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(settings.openingTime()).thenReturn(opening);
        Mockito.when(settings.closingTime()).thenReturn(closing);

        return new RestaurantDTO(
                1L,
                "test-token",
                "Test Restaurant",
                "1 Test Street",
                "00‑000",
                "Test City",
                Collections.emptySet(),
                settings,
                null,
                null,
                Instant.now(),
                LocalDateTime.now(),
                "unit‑test",
                "unit‑test"
        );
    }
}
