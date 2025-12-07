package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByToken(String token);

    @Query("SELECT r.settings " +
            "FROM Restaurant r " +
            "WHERE r.token = :token")
    Optional<Settings> findRestaurantSettingsByToken(String token);

    Set<Restaurant> findAllByOrganizationId(Long organizationId);

    boolean existsByToken(String token);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Restaurant r " +
            "WHERE r.id = :restaurantId " +
            "AND r.pricePlan.planType.id != 1")
    boolean hasPaidPricePlan(@Param("restaurantId") Long restaurantId);

}