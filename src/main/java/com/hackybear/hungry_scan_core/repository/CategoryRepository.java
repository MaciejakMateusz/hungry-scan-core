package com.hackybear.hungry_scan_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.hackybear.hungry_scan_core.entity.Category;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT Category from Category c WHERE c.isAvailable = true ORDER BY c.displayOrder")
    List<Category> findAllAvailable();

}
