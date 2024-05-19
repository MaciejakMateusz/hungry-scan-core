package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    @Query(value = "SELECT * FROM menu_items mi " +
            "JOIN menu_items_variants miv on mi.id = miv.menu_item_id " +
            "WHERE variants_id = :variantId LIMIT 1", nativeQuery = true)
    Optional<MenuItem> findByVariantId(@Param("variantId") Integer variantId);

    List<MenuItem> findAllByCategoryIdOrderByDisplayOrder(Integer categoryId);
}