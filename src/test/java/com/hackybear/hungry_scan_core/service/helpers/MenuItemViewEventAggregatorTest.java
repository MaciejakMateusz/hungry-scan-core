package com.hackybear.hungry_scan_core.service.helpers;

import com.hackybear.hungry_scan_core.dto.MenuItemViewCountDTO;
import com.hackybear.hungry_scan_core.interfaces.aggregators.MenuItemViewAggregation;
import com.hackybear.hungry_scan_core.repository.MenuItemViewEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemViewEventAggregatorTest {

    @Mock
    private MenuItemViewEventRepository repository;

    @InjectMocks
    private MenuItemViewEventAggregator aggregator;

    private record TestAgg(Long id, String def, String en, Integer views) implements MenuItemViewAggregation {

        @Override
        public String getDefaultTranslation() {
            return def;
        }

        @Override
        public String getTranslationEn() {
            return en;
        }
    }

    @Test
    void projectYearlyMenuItemViews_ShouldReturnEmptySet_WhenNoAggregations() {
        when(repository.aggregateByYear(42L, 2025)).thenReturn(Collections.emptyList());

        Set<MenuItemViewCountDTO> result = aggregator.projectYearlyMenuItemViews(42L, 2025);

        assertTrue(result.isEmpty());
        verify(repository).aggregateByYear(42L, 2025);
    }

    @Test
    void projectYearlyMenuItemViews_ShouldReturnCorrectDTOs_WhenAggregationsExist() {
        List<MenuItemViewAggregation> aggs = Arrays.asList(
                new TestAgg(10L, "Domyślny10", "Default10", 100),
                new TestAgg(20L, "Domyślny20", "Default20", 200)
        );
        when(repository.aggregateByYear(1L, 2024)).thenReturn(aggs);

        Set<MenuItemViewCountDTO> result = aggregator.projectYearlyMenuItemViews(1L, 2024);

        assertEquals(2, result.size());
        Map<Long, MenuItemViewCountDTO> map = result.stream()
                .collect(Collectors.toMap(MenuItemViewCountDTO::id, Function.identity()));

        MenuItemViewCountDTO dto10 = map.get(10L);
        assertNotNull(dto10);
        assertEquals("Domyślny10", dto10.defaultTranslation());
        assertEquals("Default10", dto10.translationEn());
        assertEquals(100, dto10.viewsCount());

        MenuItemViewCountDTO dto20 = map.get(20L);
        assertNotNull(dto20);
        assertEquals("Domyślny20", dto20.defaultTranslation());
        assertEquals("Default20", dto20.translationEn());
        assertEquals(200, dto20.viewsCount());

        verify(repository).aggregateByYear(1L, 2024);
    }

    @Test
    void projectMonthlyMenuItemViews_ShouldDelegateToRepositoryAndReturnDTOs() {
        List<MenuItemViewAggregation> aggs = Collections.singletonList(
                new TestAgg(5L, "M-Def", "M-En", 55)
        );
        when(repository.aggregateByMonth(7L, 2025, 3)).thenReturn(aggs);

        Set<MenuItemViewCountDTO> result = aggregator.projectMonthlyMenuItemViews(7L, 2025, 3);

        assertEquals(1, result.size());
        MenuItemViewCountDTO dto = result.iterator().next();
        assertEquals(5L, dto.id());
        assertEquals("M-Def", dto.defaultTranslation());
        assertEquals("M-En", dto.translationEn());
        assertEquals(55, dto.viewsCount());

        verify(repository).aggregateByMonth(7L, 2025, 3);
    }

    @Test
    void projectWeeklyMenuItemViews_ShouldDelegateToRepositoryAndReturnDTOs() {
        List<MenuItemViewAggregation> aggs = Collections.singletonList(
                new TestAgg(99L, "W-Def", "W-En", 999)
        );
        when(repository.aggregateByWeek(8L, 2025, 15)).thenReturn(aggs);

        Set<MenuItemViewCountDTO> result = aggregator.projectWeeklyMenuItemViews(8L, 2025, 15);

        assertEquals(1, result.size());
        MenuItemViewCountDTO dto = result.iterator().next();
        assertEquals(99L, dto.id());
        assertEquals("W-Def", dto.defaultTranslation());
        assertEquals("W-En", dto.translationEn());
        assertEquals(999, dto.viewsCount());

        verify(repository).aggregateByWeek(8L, 2025, 15);
    }

    @Test
    void projectDailyMenuItemViews_ShouldDelegateToRepositoryAndReturnDTOs() {
        LocalDate date = LocalDate.of(2025, 5, 1);
        List<MenuItemViewAggregation> aggs = Arrays.asList(
                new TestAgg(1L, "D-Def1", "D-En1", 11),
                new TestAgg(2L, "D-Def2", "D-En2", 22)
        );
        when(repository.aggregateByDay(99L, date)).thenReturn(aggs);

        Set<MenuItemViewCountDTO> result = aggregator.projectDailyMenuItemViews(99L, date);

        assertEquals(2, result.size());
        Map<Long, MenuItemViewCountDTO> map = result.stream()
                .collect(Collectors.toMap(MenuItemViewCountDTO::id, Function.identity()));

        assertEquals(11, map.get(1L).viewsCount());
        assertEquals("D-En2", map.get(2L).translationEn());

        verify(repository).aggregateByDay(99L, date);
    }

    @Test
    void projectMonthlyMenuItemViews_ShouldReturnEmptySet_WhenNoAggregations() {
        when(repository.aggregateByMonth(5L, 2025, 12))
                .thenReturn(Collections.emptyList());

        Set<MenuItemViewCountDTO> result = aggregator.projectMonthlyMenuItemViews(5L, 2025, 12);

        assertTrue(result.isEmpty());
        verify(repository).aggregateByMonth(5L, 2025, 12);
    }

    @Test
    void projectWeeklyMenuItemViews_ShouldReturnEmptySet_WhenNoAggregations() {
        when(repository.aggregateByWeek(6L, 2025, 20))
                .thenReturn(Collections.emptyList());

        Set<MenuItemViewCountDTO> result = aggregator.projectWeeklyMenuItemViews(6L, 2025, 20);

        assertTrue(result.isEmpty());
        verify(repository).aggregateByWeek(6L, 2025, 20);
    }

    @Test
    void projectDailyMenuItemViews_ShouldReturnEmptySet_WhenNoAggregations() {
        LocalDate date = LocalDate.of(2025, 5, 10);
        when(repository.aggregateByDay(8L, date))
                .thenReturn(Collections.emptyList());

        Set<MenuItemViewCountDTO> result = aggregator.projectDailyMenuItemViews(8L, date);

        assertTrue(result.isEmpty());
        verify(repository).aggregateByDay(8L, date);
    }

    @Test
    void projectYearlyMenuItemViews_ShouldThrowException_WhenDuplicateIds() {
        List<MenuItemViewAggregation> aggs = Arrays.asList(
                new TestAgg(1L, "DefA", "EnA", 10),
                new TestAgg(1L, "DefB", "EnB", 20)
        );
        when(repository.aggregateByYear(3L, 2025)).thenReturn(aggs);

        assertThrows(IllegalStateException.class,
                () -> aggregator.projectYearlyMenuItemViews(3L, 2025),
                "Expected duplicate IDs to cause an IllegalStateException");
        verify(repository).aggregateByYear(3L, 2025);
    }

    @Test
    void projectWeeklyMenuItemViews_ShouldHandleNullTranslationFields() {
        List<MenuItemViewAggregation> aggs = Collections.singletonList(
                new TestAgg(7L, null, null, 0)
        );
        when(repository.aggregateByWeek(4L, 2025, 10)).thenReturn(aggs);

        Set<MenuItemViewCountDTO> result = aggregator.projectWeeklyMenuItemViews(4L, 2025, 10);

        assertEquals(1, result.size());
        MenuItemViewCountDTO dto = result.iterator().next();
        assertNull(dto.defaultTranslation(), "Default translation should be null");
        assertNull(dto.translationEn(), "English translation should be null");
        assertEquals(0, dto.viewsCount(), "View count should still be 0");

        verify(repository).aggregateByWeek(4L, 2025, 10);
    }
}
