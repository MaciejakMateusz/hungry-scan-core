package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    List<MenuItem> findAllByCategoryIdOrderByDisplayOrder(Integer categoryId);
}