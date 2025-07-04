package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Covers every public-facing branch of MenuPlanUpdater:
 * 1.  Head  extension
 * 2.  Head  extension – prev. day overnight
 * 3.  Head  shrink
 * 4.  Head  shrink   – prev. day overnight
 * 5.  Tail  extension
 * 6.  Tail  extension – overnight (→ extra plan on next day)
 * 7.  Tail  extension – overnight, prev. day already overnight
 * 8.  Tail  shrink
 * 9.  Tail  shrink    – overnight (shorter)
 * 10. Tail  shrink    – overnight ➜ NOT overnight (00-03 removed)
 * 11. Tail  shrink    – overnight, prev. day also overnight
 * 12. Day no longer available (normal)
 * 13. Day no longer available (overnight, remove 00-03 from next day)
 * 14. Day newly available (normal)
 * 15. Day newly available (overnight, create 00-03 on next day)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuPlanUpdaterTest {

    private MenuPlanUpdater updater;

    @BeforeEach
    void setup() {
        updater = new MenuPlanUpdater();
    }

    /* ---------- 1 & 2 ---------- */

    @Test
    void operatingHoursHeadExtension_updatesOnlyStartTime() {
        Scenario s = Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("08:00", "17:00"))
                .assertion(r -> {
                    TimeRange rng = singleRange(r, DayOfWeek.MONDAY);
                    assertThat(rng.getStartTime()).isEqualTo(t("08:00"));
                    assertThat(rng.getEndTime()).isEqualTo(t("17:00"));
                })
                .build();

        s.run(updater);
    }

    @Test
    void headExtension_keepsPreviousOvernightIntact() {
        Scenario s = Scenario.builder()
                .day(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .newDay(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("08:00", "17:00"))
                .assertion(r -> {
                    TimeRange mon = singleRange(r, DayOfWeek.MONDAY);
                    TimeRange sun = singleRange(r, DayOfWeek.SUNDAY);
                    assertThat(mon.getStartTime()).isEqualTo(t("08:00"));
                    assertThat(sun.getStartTime()).isEqualTo(t("22:00"));
                    assertThat(sun.getEndTime()).isEqualTo(t("02:00"));
                })
                .build();

        s.run(updater);
    }

    /* ---------- 3 & 4 ---------- */

    @Test
    void operatingHoursHeadShrink_movesStartLater() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("10:00", "17:00"))
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.MONDAY).getStartTime())
                                .isEqualTo(t("10:00")))
                .build()
                .run(updater);
    }

    @Test
    void headShrink_withPreviousOvernight() {
        Scenario.builder()
                .day(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .newDay(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("10:00", "17:00"))
                .assertion(r -> {
                    assertThat(singleRange(r, DayOfWeek.MONDAY).getStartTime())
                            .isEqualTo(t("10:00"));
                    assertThat(singleRange(r, DayOfWeek.SUNDAY).getEndTime())
                            .isEqualTo(t("02:00"));
                })
                .build()
                .run(updater);
    }

    /* ---------- 5 ---------- */

    @Test
    void tailExtension_sameDay() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("09:00", "18:00"))
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.MONDAY).getEndTime())
                                .isEqualTo(t("18:00")))
                .build()
                .run(updater);
    }

    /* ---------- 6 ---------- */

    @Test
    void tailExtension_overnight_createsNextDayPlan() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("09:00", "22:00"))
                .newDay(DayOfWeek.MONDAY, tr("09:00", "02:00"))
                .assertion(r -> {
                    assertThat(singleRange(r, DayOfWeek.MONDAY).getEndTime())
                            .isEqualTo(LocalTime.MIDNIGHT);
                    TimeRange tue = singleRange(r, DayOfWeek.TUESDAY);
                    assertThat(tue.getStartTime()).isEqualTo(LocalTime.MIDNIGHT);
                    assertThat(tue.getEndTime()).isEqualTo(t("02:00"));
                })
                .build()
                .run(updater);
    }

    /* ---------- 7 ---------- */

    @Test
    void tailExtension_overnight_previousDayOvernight() {
        Scenario.builder()
                .day(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .newDay(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .day(DayOfWeek.MONDAY, tr("09:00", "02:00"))
                .newDay(DayOfWeek.MONDAY, tr("09:00", "03:00"))
                .withOvernightSplit(DayOfWeek.MONDAY)
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.TUESDAY).getEndTime())
                                .isEqualTo(t("03:00")))
                .build()
                .run(updater);
    }

    /* ---------- 8 ---------- */

    @Test
    void tailShrink_sameDay() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("09:00", "17:00"))
                .newDay(DayOfWeek.MONDAY, tr("09:00", "16:00"))
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.MONDAY).getEndTime())
                                .isEqualTo(t("16:00")))
                .build()
                .run(updater);
    }

    /* ---------- 9 ---------- */

    @Test
    void tailShrink_overnight_shorterNextDay() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("12:00", "03:00"))
                .newDay(DayOfWeek.MONDAY, tr("12:00", "01:00"))
                .withOvernightSplit(DayOfWeek.MONDAY)
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.TUESDAY).getEndTime())
                                .isEqualTo(t("01:00")))
                .build()
                .run(updater);
    }

    /* ---------- 10 ---------- */

    @Test
    void tailShrink_overnightIntoSameDay_removesNextDayMidnightRange() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("12:00", "03:00"))
                .newDay(DayOfWeek.MONDAY, tr("12:00", "22:00"))
                .withOvernightSplit(DayOfWeek.MONDAY)
                .assertion(r -> {
                    assertThat(singleRange(r, DayOfWeek.MONDAY).getEndTime())
                            .isEqualTo(t("22:00"));
                    assertThat(getPlan(r, DayOfWeek.TUESDAY)).isNull();
                })
                .build()
                .run(updater);
    }

    /* ---------- 11 ---------- */

    @Test
    void tailShrink_overnight_prevDayOvernight() {
        Scenario.builder()
                .day(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .newDay(DayOfWeek.SUNDAY, tr("22:00", "02:00"))
                .day(DayOfWeek.MONDAY, tr("12:00", "03:00"))
                .newDay(DayOfWeek.MONDAY, tr("12:00", "02:00"))
                .withOvernightSplit(DayOfWeek.MONDAY)
                .assertion(r ->
                        assertThat(singleRange(r, DayOfWeek.TUESDAY).getEndTime())
                                .isEqualTo(t("02:00")))
                .build()
                .run(updater);
    }

    /* ---------- 12 ---------- */

    @Test
    void dayNoLongerAvailable_normalHours() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("12:00", "22:00"))
                .newDayClosed(DayOfWeek.MONDAY)
                .assertion(r ->
                        assertThat(getPlan(r, DayOfWeek.MONDAY)).isNull())
                .build()
                .run(updater);
    }

    /* ---------- 13 ---------- */

    @Test
    void dayNoLongerAvailable_overnight_removesNextDayMidnight() {
        Scenario.builder()
                .day(DayOfWeek.MONDAY, tr("12:00", "03:00"))
                .newDayClosed(DayOfWeek.MONDAY)
                .withOvernightSplit(DayOfWeek.MONDAY)
                .assertion(r -> {
                    assertThat(getPlan(r, DayOfWeek.MONDAY)).isNull();
                    assertThat(getPlan(r, DayOfWeek.TUESDAY)).isNull();
                })
                .build()
                .run(updater);
    }

    /* ---------- 14 ---------- */

    @Test
    void dayNewlyAvailable_normalHours() {
        Scenario.builder()
                .dayClosed(DayOfWeek.MONDAY)
                .newDay(DayOfWeek.MONDAY, tr("12:00", "22:00"))
                .assertion(r -> {
                    TimeRange m = singleRange(r, DayOfWeek.MONDAY);
                    assertThat(m.getStartTime()).isEqualTo(t("12:00"));
                    assertThat(m.getEndTime()).isEqualTo(t("22:00"));
                })
                .build()
                .run(updater);
    }

    /* ---------- 15 ---------- */

    @Test
    void dayNewlyAvailable_overnight_createsNextDayMidnight() {
        Scenario.builder()
                .dayClosed(DayOfWeek.MONDAY)
                .newDay(DayOfWeek.MONDAY, tr("12:00", "03:00"))
                .assertion(r -> {
                    TimeRange mon = singleRange(r, DayOfWeek.MONDAY);
                    TimeRange tue = singleRange(r, DayOfWeek.TUESDAY);
                    assertThat(mon.getStartTime()).isEqualTo(t("12:00"));
                    assertThat(mon.getEndTime()).isEqualTo(LocalTime.MIDNIGHT);
                    assertThat(tue.getStartTime()).isEqualTo(LocalTime.MIDNIGHT);
                    assertThat(tue.getEndTime()).isEqualTo(t("03:00"));
                })
                .build()
                .run(updater);
    }

    /* --------------------------------------------------------------------- *
     *                       ===  Test-helpers  ===                           *
     * --------------------------------------------------------------------- */

    private static final class Scenario {

        private final Map<DayOfWeek, TimeRange> oldHours = new EnumMap<>(DayOfWeek.class);
        private final Map<DayOfWeek, TimeRange> newHours = new EnumMap<>(DayOfWeek.class);
        private final Set<DayOfWeek> splitOvernight = new HashSet<>();
        private java.util.function.Consumer<Restaurant> assertion = r -> {
        };

        private Scenario() {
        }

        static Builder builder() {
            return new Builder(new Scenario());
        }

        void run(MenuPlanUpdater updater) {
            Restaurant restaurant = buildRestaurant(oldHours, splitOvernight);
            RestaurantDTO dto = mockDTO(newHours);
            updater.updateMenusPlans(restaurant, dto);
            assertion.accept(restaurant);
        }

        static final class Builder {
            private final Scenario s;

            Builder(Scenario s) {
                this.s = s;
            }

            Builder day(DayOfWeek d, TimeRange tr) {
                s.oldHours.put(d, tr);
                return this;
            }

            Builder dayClosed(DayOfWeek d) {
                s.oldHours.put(d, closed());
                return this;
            }

            Builder newDay(DayOfWeek d, TimeRange tr) {
                s.newHours.put(d, tr);
                return this;
            }

            Builder newDayClosed(DayOfWeek d) {
                s.newHours.put(d, closed());
                return this;
            }

            Builder withOvernightSplit(DayOfWeek d) {
                s.splitOvernight.add(d);
                return this;
            }

            Builder assertion(java.util.function.Consumer<Restaurant> a) {
                s.assertion = a;
                return this;
            }

            Scenario build() {
                for (DayOfWeek d : DayOfWeek.values()) {
                    s.oldHours.computeIfAbsent(d, k -> closed());
                    s.newHours.computeIfAbsent(d, k -> closed());
                }
                return s;
            }
        }
    }

    private static Restaurant buildRestaurant(Map<DayOfWeek, TimeRange> oldHours,
                                              Set<DayOfWeek> splitOvernight) {
        Settings settings = new Settings();
        settings.setOperatingHours(oldHours);

        Menu menu = new Menu();
        menu.setStandard(true);
        menu.setPlan(new HashSet<>());

        for (DayOfWeek d : oldHours.keySet()) {
            TimeRange tr = oldHours.get(d);
            if (!tr.isAvailable()) continue;

            menu.getPlan().add(planFor(menu, d, tr));

            if (splitOvernight.contains(d) && isOvernight(tr)) {
                menu.getPlan().add(planFor(menu, d.plus(1),
                        new TimeRange(LocalTime.MIDNIGHT, tr.getEndTime())));
            }
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setSettings(settings);
        restaurant.setMenus(Set.of(menu));
        return restaurant;
    }

    private static MenuPlan planFor(Menu m, DayOfWeek d, TimeRange tr) {
        MenuPlan p = new MenuPlan();
        p.setMenu(m);
        p.setDayOfWeek(d);
        p.setTimeRanges(new HashSet<>(Set.of(tr)));
        return p;
    }

    private static RestaurantDTO mockDTO(Map<DayOfWeek, TimeRange> newHours) {
        SettingsDTO settingsDTO = Mockito.mock(SettingsDTO.class);
        when(settingsDTO.operatingHours()).thenReturn(newHours);

        RestaurantDTO dto = Mockito.mock(RestaurantDTO.class);
        when(dto.settings()).thenReturn(settingsDTO);
        return dto;
    }

    private static LocalTime t(String hhmm) {
        return LocalTime.parse(hhmm);
    }

    private static TimeRange singleRange(Restaurant r, DayOfWeek d) {
        MenuPlan p = getPlan(r, d);
        assertThat(p).withFailMessage("No MenuPlan for %s", d).isNotNull();
        assertThat(p.getTimeRanges()).hasSize(1);
        return p.getTimeRanges().iterator().next();
    }

    private static MenuPlan getPlan(Restaurant r, DayOfWeek d) {
        return r.getMenus().iterator().next().getPlan().stream()
                .filter(p -> p.getDayOfWeek().equals(d))
                .findFirst()
                .orElse(null);
    }

    private static TimeRange tr(String start, String end) {
        return new TimeRange(t(start), t(end));
    }

    private static TimeRange closed() {
        TimeRange tr = new TimeRange(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        tr.setAvailable(false);
        return tr;
    }

    private static boolean isOvernight(TimeRange tr) {
        return tr.getEndTime().isBefore(tr.getStartTime());
    }
}
