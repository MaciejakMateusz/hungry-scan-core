package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    @Query("SELECT mi from MenuItem mi WHERE mi.name.defaultTranslation LIKE LOWER(:filterValue)  ORDER BY mi.name.defaultTranslation")
    List<MenuItem> filterByName(@Param("filterValue") String filterValue);
}
