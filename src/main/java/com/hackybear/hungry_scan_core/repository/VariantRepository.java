package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findAllByMenuItemIdOrderByDisplayOrder(Long menuItemId);

    @Query("SELECT v.menuItem FROM Variant v WHERE v.menuItem.category.menu.id = :menuId")
    List<MenuItem> findAllByMenuId(@Param("menuId") Long menuId);

}