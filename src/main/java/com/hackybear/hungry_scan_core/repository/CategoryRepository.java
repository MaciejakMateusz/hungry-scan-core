package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT Category from Category c WHERE c.isAvailable = true ORDER BY c.displayOrder")
    List<Category> findAllAvailable();

    @Query("SELECT c FROM Category c WHERE :menuItem MEMBER OF c.menuItems")
    Category findByMenuItem(@Param("menuItem") MenuItem menuItem);

    List<Category> findAllByOrderByDisplayOrder();

    @Query("SELECT c.displayOrder FROM Category c")
    List<Integer> findAllDisplayOrders();
}