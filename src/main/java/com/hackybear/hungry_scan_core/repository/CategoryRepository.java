package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.menuItems WHERE c.id = :id")
    Optional<Category> findByIdFetchMenuItems(@Param("id") Long id);

    @Query("SELECT c from Category c WHERE c.available = true AND c.menu.id = :menuId ORDER BY c.displayOrder")
    List<Category> findAllAvailableByMenuId(@Param("menuId") Long menuId);

    @Query("""
            SELECT c FROM Category c
            JOIN FETCH c.menuItems
            WHERE c.id = (SELECT m.category.id FROM MenuItem m WHERE m.id = :menuItemId)""")
    Optional<Category> findByMenuItemId(@Param("menuItemId") Long menuItemId);

    Set<Category> findAllByMenuId(@Param("menuId") Long menuId);

    @Query("SELECT c.displayOrder FROM Category c WHERE c.menu.id = :menuId ORDER BY c.displayOrder")
    List<Integer> findAllDisplayOrdersByMenuId(@Param("menuId") Long menuId);

    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.menu.id = :menuId")
    Optional<Integer> findMaxDisplayOrderByMenuId(@Param("menuId") Long menuId);

    Long countByMenuId(Long menuId);

    @Modifying
    @Query("UPDATE Category c SET c.displayOrder = :displayOrder WHERE c.id = :categoryId")
    void updateDisplayOrders(@Param("categoryId") Long categoryId, @Param("displayOrder") Integer displayOrder);

}