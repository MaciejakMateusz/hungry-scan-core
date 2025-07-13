package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.MenuPlanDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuPlanValidatorTest {

    @Mock
    SettingsRepository settingsRepository;
    @Mock
    ExceptionHelper exceptionHelper;

    @InjectMocks
    MenuPlanValidator validator;

    @BeforeEach
    void setUp() throws LocalizedException {
        doAnswer(inv -> {
            throw new LocalizedException(inv.getArgument(0));
        })
                .when(exceptionHelper)
                .throwLocalizedMessage(anyString());
    }

    private TimeRange timeRange(LocalTime start, LocalTime end) {
        TimeRange tr = mock(TimeRange.class);
        when(tr.getStartTime()).thenReturn(start);
        when(tr.getEndTime()).thenReturn(end);
        when(tr.isAvailable()).thenReturn(true);
        return tr;
    }

    private Settings settingsWithOperatingHours(TimeRange range) {
        Settings s = mock(Settings.class);
        Map<DayOfWeek, TimeRange> map = new EnumMap<>(DayOfWeek.class);
        map.put(DayOfWeek.MONDAY, range);
        when(s.getOperatingHours()).thenReturn(map);
        return s;
    }

    private MenuSimpleDTO menu(MenuPlanDTO... plans) {
        MenuSimpleDTO dto = mock(MenuSimpleDTO.class);
        when(dto.plan()).thenReturn(Set.of(plans));
        return dto;
    }

    @Test
    void validateMenusPlans_throwsWhenAllSchedulesEmpty() {
        MenuSimpleDTO menu = menu();
        when(settingsRepository.findByRestaurantId(anyLong()))
                .thenReturn(mock(Settings.class));

        LocalizedException ex = assertThrows(
                LocalizedException.class,
                () -> validator.validateMenusPlans(List.of(menu), 123L));

        assertEquals("error.menuService.scheduleIncomplete", ex.getMessage());
    }

    @Test
    void validateMenusPlans_throwsWhenOutsideOpeningHours() {
        Settings settings = settingsWithOperatingHours(
                timeRange(LocalTime.of(8, 0), LocalTime.of(16, 0)));
        when(settingsRepository.findByRestaurantId(1L)).thenReturn(settings);

        MenuPlanDTO plan = new MenuPlanDTO(
                null,
                1L,
                DayOfWeek.MONDAY,
                Set.of(timeRange(LocalTime.of(7, 0), LocalTime.of(9, 0))));
        MenuSimpleDTO menu = menu(plan);

        LocalizedException ex = assertThrows(
                LocalizedException.class,
                () -> validator.validateMenusPlans(List.of(menu), 1L));

        assertEquals("error.menuService.scheduleNotWithinOpeningHours", ex.getMessage());
    }

    @Test
    void validateMenusPlans_throwsWhenCoverageIncomplete() {
        Settings settings = settingsWithOperatingHours(
                timeRange(LocalTime.of(8, 0), LocalTime.of(10, 0)));
        when(settingsRepository.findByRestaurantId(1L)).thenReturn(settings);

        MenuPlanDTO plan = new MenuPlanDTO(
                null,
                1L,
                DayOfWeek.MONDAY,
                Set.of(timeRange(LocalTime.of(8, 0), LocalTime.of(9, 0))));
        MenuSimpleDTO menu = menu(plan);

        LocalizedException ex = assertThrows(
                LocalizedException.class,
                () -> validator.validateMenusPlans(List.of(menu), 1L));

        assertEquals("error.menuService.scheduleIncomplete", ex.getMessage());
    }
}