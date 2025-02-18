package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Set<Menu> findAllByRestaurantId(Long restaurantId);

    @Query(value = """
            SELECT s.menu_id
            FROM schedule s
            JOIN schedule_plan sp ON s.id = sp.schedule_id
            JOIN menus m ON s.menu_id = m.id
            WHERE m.restaurant_id = :restaurantId
              AND (
                   (sp.plan_key = :dayOfWeekOrdinal
                    AND sp.start_time <= :currentTime
                    AND sp.end_time >= :currentTime
                   )
                   OR m.is_all_day = true
                  )
            """, nativeQuery = true)
    Optional<Long> findActiveMenuId(@Param("dayOfWeekOrdinal") int dayOfWeekOrdinal,
                                    @Param("currentTime") LocalTime currentTime,
                                    @Param("restaurantId") Long restaurantId);
}