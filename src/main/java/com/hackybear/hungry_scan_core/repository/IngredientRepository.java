package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
}
