package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @NonNull
    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.plan LEFT JOIN FETCH m.plan WHERE m.id = :id")
    Optional<Menu> findById(@NonNull @Param("id") Long id);

    @Query("""
            SELECT m
            FROM Menu m
            LEFT JOIN FETCH m.categories c
            LEFT JOIN FETCH c.menuItems mi
            WHERE m.id = :id
            """)
    Optional<Menu> findByIdWithAllGraph(@Param("id") Long id);

    Set<Menu> findAllByRestaurantId(Long restaurantId);

    @Query("""
              SELECT DISTINCT m.id
              FROM Menu m
                JOIN m.plan p
                JOIN p.timeRanges tr
              WHERE m.restaurant.id    = :restaurantId
                AND p.dayOfWeek         = :dayOfWeek
                AND (
                     (tr.startTime <= :currentTime AND tr.endTime >= :currentTime)
                     OR
                     (tr.startTime >  tr.endTime
                      AND (:currentTime >= tr.startTime OR :currentTime <= tr.endTime))
                    )
            """)
    Optional<Long> findActiveMenuId(@Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("currentTime") LocalTime currentTime,
                                    @Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("UPDATE Menu m SET m.standard = false WHERE m.restaurant.id = :restaurantId AND m.standard = true")
    void resetStandardMenus(@Param("restaurantId") Long restaurantId);

    @Modifying
    @Query("UPDATE Menu m SET m.standard = true WHERE m.id = :newId")
    void switchStandard(@Param("newId") Long newId);

    @Query("SELECT m.id FROM Menu m WHERE m.standard = true AND m.restaurant.id = :restaurantId")
    Optional<Long> findStandardIdByRestaurantId(@Param("restaurantId") Long restaurantId);

    boolean existsByRestaurantIdAndName(Long restaurantId, String name);

    Integer countByRestaurantId(Long restaurantId);

}