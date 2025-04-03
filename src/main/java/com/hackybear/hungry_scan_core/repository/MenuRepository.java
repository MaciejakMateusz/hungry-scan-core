package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @NonNull
    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.plan WHERE m.id = :id")
    Optional<Menu> findById(@NonNull @Param("id") Long id);

    Set<Menu> findAllByRestaurantId(Long restaurantId);

    @Query(value = """
            SELECT m.id
            FROM menus m
            LEFT JOIN menu_plan sp ON m.id = sp.menu_id
            WHERE m.restaurant_id = :restaurantId
            AND (
                m.standard = TRUE
                OR (sp.plan_key = :dayOfWeekOrdinal
                    AND sp.start_time <= :currentTime
                    AND sp.end_time >= :currentTime)
                )
            """, nativeQuery = true)
    Optional<Long> findActiveMenuId(@Param("dayOfWeekOrdinal") int dayOfWeekOrdinal,
                                    @Param("currentTime") LocalTime currentTime,
                                    @Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("UPDATE Menu m SET m.standard = false WHERE m.restaurantId = :restaurantId AND m.standard = true")
    void resetStandardMenus(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("UPDATE Menu m SET m.standard = true WHERE m.id = :newId")
    void switchStandard(@Param("newId") Long newId);
}