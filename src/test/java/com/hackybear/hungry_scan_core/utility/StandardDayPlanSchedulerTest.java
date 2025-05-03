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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandardDayPlanSchedulerTest {

    @Mock
    MenuRepository menuRepository;
    @Mock
    MenuMapper menuMapper;
    @Mock
    SettingsRepository settingsRepository;
    @Mock
    ExceptionHelper exceptionHelper;

    @InjectMocks
    StandardDayPlanScheduler scheduler;

    @Captor
    ArgumentCaptor<Menu> menuCaptor;

    @Test
    void mapStandardPlan_withoutStandardMenu_shouldThrow() {
        MenuSimpleDTO dto = mock(MenuSimpleDTO.class);
        when(dto.standard()).thenReturn(false);

        when(exceptionHelper.supplyLocalizedMessage(anyString()))
                .thenReturn(() -> new LocalizedException("menu.not.found"));

        LocalizedException ex = assertThrows(
                LocalizedException.class,
                () -> scheduler.mapStandardPlan(singletonList(dto))
        );
        assertEquals("menu.not.found", ex.getMessage());
    }

    @Test
    void mapStandardPlan_onlyStandardMenu_normalHours_createsFullDayPlan() throws Exception {
        MenuSimpleDTO stdDto = mock(MenuSimpleDTO.class);
        doReturn(true).when(stdDto).standard();

        Menu standardMenu = spy(new Menu());
        when(standardMenu.getId()).thenReturn(1L);
        when(standardMenu.getRestaurant()).thenReturn(new Restaurant(1L));
        when(menuMapper.toMenu(stdDto)).thenReturn(standardMenu);

        Settings settings = new Settings();
        settings.setOpeningTime(LocalTime.of(8, 0));
        settings.setClosingTime(LocalTime.of(20, 0));
        when(settingsRepository.findByRestaurantId(anyLong()))
                .thenReturn(settings);

        when(menuRepository.findById(1L))
                .thenReturn(Optional.of(standardMenu));

        scheduler.mapStandardPlan(singletonList(stdDto));

        ArgumentCaptor<Menu> captor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(captor.capture());
        Menu saved = captor.getValue();

        List<StandardDayPlan> plans = saved.getStandardDayPlan();
        assertEquals(7, plans.size());
        for (StandardDayPlan p : plans) {
            List<DayTimeRange> ranges = p.getTimeRanges();
            assertEquals(1, ranges.size());
            assertEquals(LocalTime.of(8, 0), ranges.getFirst().getStartTime());
            assertEquals(LocalTime.of(20, 0), ranges.getFirst().getEndTime());
        }
    }

    @Test
    void mapStandardPlan_crossMidnightBlocks_correctForFriday() throws Exception {
        MenuSimpleDTO stdDto = mock(MenuSimpleDTO.class);
        when(stdDto.standard()).thenReturn(true);
        Menu standardMenu = Mockito.spy(new Menu());
        when(standardMenu.getId()).thenReturn(1L);
        when(menuMapper.toMenu(stdDto)).thenReturn(standardMenu);

        MenuSimpleDTO dailyDto = mock(MenuSimpleDTO.class);
        when(dailyDto.standard()).thenReturn(false);
        Menu dailyMenu = mock(Menu.class);
        Map<DayOfWeek, TimeRange> dailyPlan = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            dailyPlan.put(d, new TimeRange(LocalTime.of(15, 0), LocalTime.of(17, 0)));
        }
        when(menuMapper.toMenu(dailyDto)).thenReturn(dailyMenu);
        when(dailyMenu.getPlan()).thenReturn(dailyPlan);

        MenuSimpleDTO nightFri = mock(MenuSimpleDTO.class);
        when(nightFri.standard()).thenReturn(false);
        Menu nightMenuFri = mock(Menu.class);
        when(menuMapper.toMenu(nightFri)).thenReturn(nightMenuFri);
        when(nightMenuFri.getPlan()).thenReturn(
                Collections.singletonMap(
                        DayOfWeek.FRIDAY,
                        new TimeRange(LocalTime.of(22, 0), LocalTime.of(3, 0))
                )
        );

        MenuSimpleDTO nightSat = mock(MenuSimpleDTO.class);
        when(nightSat.standard()).thenReturn(false);
        Menu nightMenuSat = mock(Menu.class);
        when(menuMapper.toMenu(nightSat)).thenReturn(nightMenuSat);
        when(nightMenuSat.getPlan()).thenReturn(
                Collections.singletonMap(
                        DayOfWeek.SATURDAY,
                        new TimeRange(LocalTime.of(22, 0), LocalTime.of(3, 0))
                )
        );

        when(menuRepository.findById(1L))
                .thenReturn(Optional.of(standardMenu));

        List<MenuSimpleDTO> all = Arrays.asList(stdDto, dailyDto, nightFri, nightSat);
        scheduler.mapStandardPlan(all, new TimeRange(LocalTime.of(15, 0), LocalTime.of(4, 0)));

        verify(menuRepository).save(menuCaptor.capture());
        Menu saved = menuCaptor.getValue();

        Optional<StandardDayPlan> friday = saved.getStandardDayPlan().stream()
                .filter(p -> p.getDayOfWeek() == DayOfWeek.FRIDAY)
                .findFirst();
        assertTrue(friday.isPresent());

        List<DayTimeRange> fr = friday.get().getTimeRanges();
        assertEquals(2, fr.size(), "should be two available windows on Friday");
        assertEquals(LocalTime.of(3, 0), fr.get(0).getStartTime());
        assertEquals(LocalTime.of(4, 0), fr.get(0).getEndTime());
        assertEquals(LocalTime.of(17, 0), fr.get(1).getStartTime());
        assertEquals(LocalTime.of(22, 0), fr.get(1).getEndTime());
    }
}
