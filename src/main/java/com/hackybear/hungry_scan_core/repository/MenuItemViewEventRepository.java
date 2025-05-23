package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItemViewEvent;
import com.hackybear.hungry_scan_core.interfaces.aggregators.MenuItemViewAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MenuItemViewEventRepository extends JpaRepository<MenuItemViewEvent, Long> {

    @Query(value =
            "SELECT "
                    + "  t.pl AS pl, "
                    + "  t.en AS en, "
                    + "  t.fr AS fr, "
                    + "  t.de AS de, "
                    + "  t.es AS es, "
                    + "  t.uk AS uk, "
                    + "  mi.id AS id, "
                    + "  COUNT(*) AS views "
                    + "FROM menu_item_view_events mive "
                    + "JOIN menu_items mi ON mive.menu_item_id = mi.id "
                    + "JOIN translatable t ON mi.translatable_name_id = t.id "
                    + "WHERE mive.menu_id = :menuId "
                    + "  AND YEAR(mive.viewed_at) = :year "
                    + "GROUP BY mi.id, t.en, t.fr, t.de, t.es, t.uk, t.pl "
                    + "ORDER BY views",
            nativeQuery = true)
    List<MenuItemViewAggregation> aggregateByYear(@Param("menuId") Long menuId,
                                                  @Param("year") int year);

    @Query(value =
            "SELECT "
                    + "  t.pl AS pl, "
                    + "  t.en AS en, "
                    + "  t.fr AS fr, "
                    + "  t.de AS de, "
                    + "  t.es AS es, "
                    + "  t.uk AS uk, "
                    + "  mi.id AS id, "
                    + "  COUNT(*) AS views "
                    + "FROM menu_item_view_events mive "
                    + "JOIN menu_items mi ON mive.menu_item_id = mi.id "
                    + "JOIN translatable t ON mi.translatable_name_id = t.id "
                    + "WHERE mive.menu_id = :menuId "
                    + "  AND YEAR(mive.viewed_at) = :year "
                    + "  AND MONTH(mive.viewed_at) = :month "
                    + "GROUP BY mi.id, t.en, t.fr, t.de, t.es, t.uk, t.pl "
                    + "ORDER BY views",
            nativeQuery = true)
    List<MenuItemViewAggregation> aggregateByMonth(@Param("menuId") Long menuId,
                                                   @Param("year") int year,
                                                   @Param("month") int month);

    @Query(value =
            "SELECT "
                    + "  t.pl AS pl, "
                    + "  t.en AS en, "
                    + "  t.fr AS fr, "
                    + "  t.de AS de, "
                    + "  t.es AS es, "
                    + "  t.uk AS uk, "
                    + "  mi.id AS id, "
                    + "  COUNT(*) AS views "
                    + "FROM menu_item_view_events mive "
                    + "JOIN menu_items mi ON mive.menu_item_id = mi.id "
                    + "JOIN translatable t ON mi.translatable_name_id = t.id "
                    + "WHERE mive.menu_id = :menuId "
                    + "  AND YEAR(mive.viewed_at) = :year "
                    + "  AND WEEK(mive.viewed_at) = :week "
                    + "GROUP BY mi.id, t.pl, t.en, t.fr, t.de, t.es, t.uk "
                    + "ORDER BY views",
            nativeQuery = true)
    List<MenuItemViewAggregation> aggregateByWeek(@Param("menuId") Long menuId,
                                                  @Param("year") int year,
                                                  @Param("week") int week);

    @Query(value =
            "SELECT "
                    + "  t.pl AS pl, "
                    + "  t.en AS en, "
                    + "  t.fr AS fr, "
                    + "  t.de AS de, "
                    + "  t.es AS es, "
                    + "  t.uk AS uk, "
                    + "  mi.id AS id, "
                    + "  COUNT(*) AS views "
                    + "FROM menu_item_view_events mive "
                    + "JOIN menu_items mi ON mive.menu_item_id = mi.id "
                    + "JOIN translatable t ON mi.translatable_name_id = t.id "
                    + "WHERE mive.menu_id = :menuId "
                    + "AND CAST(mive.viewed_at AS DATE) = :date "
                    + "GROUP BY mi.id, t.en, t.fr, t.de, t.es, t.uk, t.pl "
                    + "ORDER BY views",
            nativeQuery = true)
    List<MenuItemViewAggregation> aggregateByDay(@Param("menuId") Long menuId,
                                                 @Param("date") LocalDate date);
}
