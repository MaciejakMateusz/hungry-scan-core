package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Set<Menu> findAllByRestaurantId(Long restaurantId);
}