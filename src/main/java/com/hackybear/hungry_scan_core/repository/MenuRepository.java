package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByRestaurantId(Long restaurantId);

    Long countAllByRestaurantId(Long restaurantId);
}