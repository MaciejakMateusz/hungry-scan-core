package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CustomRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o WHERE o.isForTakeAway = false")
    List<Order> findAllDineIn();

    @Query(value = "SELECT o FROM Order o WHERE o.isForTakeAway = true")
    List<Order> findAllTakeAway();

    List<Order> findAllByRestaurantTable(RestaurantTable restaurantTableId);

}