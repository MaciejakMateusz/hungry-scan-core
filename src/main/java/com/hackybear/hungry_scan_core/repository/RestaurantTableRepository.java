package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends CustomRepository<RestaurantTable, Integer> {

    Optional<RestaurantTable> findByToken(String token);

    Optional<RestaurantTable> findByNumber(Integer number);

    boolean existsByNumber(Integer number);
}
