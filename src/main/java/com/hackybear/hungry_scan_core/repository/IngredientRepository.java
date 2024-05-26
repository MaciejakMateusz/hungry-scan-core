package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    @Query("SELECT i FROM Ingredient i ORDER BY i.name.defaultTranslation")
    Page<Ingredient> findAllOrderByDefaultTranslation(Pageable pageable);
}
