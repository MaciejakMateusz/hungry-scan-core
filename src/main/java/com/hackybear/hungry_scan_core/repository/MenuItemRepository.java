package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT mi from MenuItem mi WHERE mi.name.pl LIKE LOWER(:filterValue) ORDER BY mi.name.pl")
    Set<MenuItem> filterByName(@Param("filterValue") String filterValue);

    Set<MenuItem> findAllByCategoryIdOrderByDisplayOrder(Long categoryId);

    @Modifying
    @Query("UPDATE MenuItem mi SET mi.displayOrder = :displayOrder WHERE mi.id = :menuItemId")
    void updateDisplayOrders(@Param("menuItemId") Long menuItemId, @Param("displayOrder") Integer displayOrder);

    @Query("SELECT MAX(mi.displayOrder) FROM MenuItem mi WHERE mi.category.id = :categoryId")
    Optional<Integer> findMaxDisplayOrder(@Param("categoryId") Long categoryId);

}