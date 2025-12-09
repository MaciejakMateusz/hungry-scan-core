package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i WHERE i.restaurant.id = :restaurantId ORDER BY i.name.pl")
    Page<Ingredient> findAllOrderByDefaultTranslation(Pageable pageable, @Param("restaurantId") Long restaurantId);

    @Query("SELECT i FROM Ingredient i WHERE i.restaurant.id = :restaurantId ORDER BY i.name.pl")
    List<Ingredient> findAllOrderByDefaultTranslation(@Param("restaurantId") Long restaurantId);

    @Query("""
            SELECT i from Ingredient i
            WHERE i.name.pl LIKE LOWER(:filterValue)
            AND i.restaurant.id = :restaurantId
            ORDER BY i.name.pl
            """)
    List<Ingredient> filterByName(@Param("filterValue") String filterValue, @Param("restaurantId") Long restaurantId);


    @Modifying
    @Query(value = "delete from menu_items_ingredients where ingredients_id = :id", nativeQuery = true)
    void deleteFromIngredientsJoin(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from menu_items_additional_ingredients where additional_ingredients_id = :id", nativeQuery = true)
    void deleteFromAdditionalJoin(@Param("id") Long id);
}
