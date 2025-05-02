package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandardDayPlanSchedulerTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private ExceptionHelper exceptionHelper;

    @InjectMocks
    private StandardDayPlanScheduler mapper;

    @Test
    void mapStandardPlan_noStandard_throwsLocalizedException() {
        when(exceptionHelper
                .supplyLocalizedMessage("error.menuService.menuNotFound"))
                .thenReturn(() -> new LocalizedException("menu not found"));

        LocalizedException ex = assertThrows(
                LocalizedException.class,
                () -> mapper.mapStandardPlan(Collections.emptyList())
        );
        assertEquals("menu not found", ex.getMessage());
    }

    @Test
    void mapStandardPlan_noNonStandard_fullAvailability() throws LocalizedException {
        MenuSimpleDTO stdDto = mock(MenuSimpleDTO.class);
        when(stdDto.standard()).thenReturn(true);

        Menu stdMenu = new Menu();
        Restaurant r = mock(Restaurant.class);
        when(r.getId()).thenReturn(42L);
        stdMenu.setRestaurant(r);
        when(menuMapper.toMenu(stdDto)).thenReturn(stdMenu);

        Settings settings = new Settings();
        settings.setOpeningTime(LocalTime.of(8, 0));
        settings.setClosingTime(LocalTime.of(18, 0));
        when(settingsRepository.findByRestaurantId(42L))
                .thenReturn(settings);

        mapper.mapStandardPlan(List.of(stdDto));

        ArgumentCaptor<Menu> cap = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(cap.capture());
        Menu saved = cap.getValue();

        List<StandardDayPlan> plans = saved.getStandardDayPlan();
        assertEquals(7, plans.size(),
                "Should have one StandardDayPlan per weekday");

        plans.forEach(p -> {
            List<DayTimeRange> tr = p.getTimeRanges();
            assertEquals(1, tr.size());
            assertEquals(LocalTime.of(8, 0), tr.getFirst().getStartTime());
            assertEquals(LocalTime.of(18, 0), tr.getFirst().getEndTime());
        });
    }

    @Test
    void mapStandardPlan_withBlockedRanges_splitsRanges() throws LocalizedException {
        MenuSimpleDTO standardMenu = mock(MenuSimpleDTO.class);
        when(standardMenu.standard()).thenReturn(true);
        Menu stdMenu = new Menu();
        Restaurant r = mock(Restaurant.class);
        when(r.getId()).thenReturn(42L);
        stdMenu.setRestaurant(r);
        when(menuMapper.toMenu(standardMenu)).thenReturn(stdMenu);

        MenuSimpleDTO additionalMenu = mock(MenuSimpleDTO.class);
        when(additionalMenu.standard()).thenReturn(false);
        Menu nonMenu = new Menu();
        Map<DayOfWeek, com.hackybear.hungry_scan_core.utility.TimeRange> plan =
                new EnumMap<>(DayOfWeek.class);
        plan.put(DayOfWeek.MONDAY,
                new com.hackybear.hungry_scan_core.utility.TimeRange(
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0)
                )
        );
        nonMenu.setPlan(plan);
        when(menuMapper.toMenu(additionalMenu)).thenReturn(nonMenu);

        Settings settings = new Settings();
        settings.setOpeningTime(LocalTime.of(8, 0));
        settings.setClosingTime(LocalTime.of(18, 0));
        when(settingsRepository.findByRestaurantId(42L))
                .thenReturn(settings);

        mapper.mapStandardPlan(List.of(standardMenu, additionalMenu));

        ArgumentCaptor<Menu> cap = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(cap.capture());
        Menu saved = cap.getValue();

        List<StandardDayPlan> plans = saved.getStandardDayPlan();
        assertEquals(7, plans.size(),
                "Should still have exactly 7 day-plans");

        StandardDayPlan monday = plans.stream()
                .filter(p -> p.getDayOfWeek() == DayOfWeek.MONDAY)
                .findFirst().orElseThrow();
        List<DayTimeRange> mr = monday.getTimeRanges();
        assertEquals(2, mr.size(), "Monday must split into two slots");

        assertEquals(LocalTime.of(8, 0), mr.getFirst().getStartTime());
        assertEquals(LocalTime.of(10, 0), mr.getFirst().getEndTime());
        assertEquals(LocalTime.of(12, 0), mr.get(1).getStartTime());
        assertEquals(LocalTime.of(18, 0), mr.get(1).getEndTime());

        StandardDayPlan tuesday = plans.stream()
                .filter(p -> p.getDayOfWeek() == DayOfWeek.TUESDAY)
                .findFirst().orElseThrow();
        List<DayTimeRange> tr = tuesday.getTimeRanges();
        assertEquals(1, tr.size());
        assertEquals(LocalTime.of(8, 0), tr.getFirst().getStartTime());
        assertEquals(LocalTime.of(18, 0), tr.getFirst().getEndTime());
    }

    @Test
    void mapStandardPlan_crossMidnight_withNonStandardBlocks() throws LocalizedException {
        MenuSimpleDTO stdDto = mock(MenuSimpleDTO.class);
        when(stdDto.standard()).thenReturn(true);
        Menu stdMenu = new Menu();
        Restaurant r = mock(Restaurant.class);
        when(r.getId()).thenReturn(42L);
        stdMenu.setRestaurant(r);
        when(menuMapper.toMenu(stdDto)).thenReturn(stdMenu);

        MenuSimpleDTO nonDto = mock(MenuSimpleDTO.class);
        when(nonDto.standard()).thenReturn(false);
        Menu nonMenu = new Menu();
        Map<DayOfWeek, com.hackybear.hungry_scan_core.utility.TimeRange> nonPlan =
                new EnumMap<>(DayOfWeek.class);
        nonPlan.put(DayOfWeek.MONDAY,
                new com.hackybear.hungry_scan_core.utility.TimeRange(
                        LocalTime.of(21, 0),
                        LocalTime.MIDNIGHT
                )
        );
        nonMenu.setPlan(nonPlan);
        when(menuMapper.toMenu(nonDto)).thenReturn(nonMenu);

        MenuSimpleDTO nonDto2 = mock(MenuSimpleDTO.class);
        when(nonDto2.standard()).thenReturn(false);
        Menu nonMenu2 = new Menu();
        Map<DayOfWeek, com.hackybear.hungry_scan_core.utility.TimeRange> nonPlan2 =
                new EnumMap<>(DayOfWeek.class);
        nonPlan2.put(DayOfWeek.MONDAY,
                new com.hackybear.hungry_scan_core.utility.TimeRange(
                        LocalTime.of(13, 0),
                        LocalTime.of(18, 0)
                )
        );
        nonMenu2.setPlan(nonPlan2);
        when(menuMapper.toMenu(nonDto2)).thenReturn(nonMenu2);

        Settings settings = new Settings();
        settings.setOpeningTime(LocalTime.of(12, 0));
        settings.setClosingTime(LocalTime.of(2, 0));
        when(settingsRepository.findByRestaurantId(42L))
                .thenReturn(settings);

        mapper.mapStandardPlan(List.of(stdDto, nonDto, nonDto2));

        ArgumentCaptor<Menu> cap = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(cap.capture());
        Menu saved = cap.getValue();

        assertTrue(nonMenu.getPlan().containsKey(DayOfWeek.MONDAY),
                "Non-standard menu should have a Monday block");
        com.hackybear.hungry_scan_core.utility.TimeRange blocked =
                nonMenu.getPlan().get(DayOfWeek.MONDAY);
        assertEquals(LocalTime.of(21, 0), blocked.getStartTime());
        assertEquals(LocalTime.MIDNIGHT, blocked.getEndTime());

        StandardDayPlan mondayPlan = saved.getStandardDayPlan().stream()
                .filter(p -> p.getDayOfWeek() == DayOfWeek.MONDAY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing MONDAY plan"));

        List<DayTimeRange> slots = new ArrayList<>(mondayPlan.getTimeRanges());
        assertEquals(3, slots.size(), "Should have three open windows after blocking");

        slots.sort(Comparator.comparing(DayTimeRange::getStartTime));

        assertEquals(LocalTime.MIDNIGHT, slots.get(0).getStartTime());
        assertEquals(LocalTime.of(2, 0), slots.getFirst().getEndTime());

        assertEquals(LocalTime.of(12, 0), slots.get(1).getStartTime());
        assertEquals(LocalTime.of(13, 0), slots.get(1).getEndTime());

        assertEquals(LocalTime.of(18, 0), slots.get(2).getStartTime());
        assertEquals(LocalTime.of(21, 0), slots.get(2).getEndTime());
    }

}
