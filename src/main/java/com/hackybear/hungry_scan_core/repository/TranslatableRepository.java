package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslatableRepository extends JpaRepository<Translatable, Integer> {

    @Query("SELECT a.name, a.description FROM Allergen a")
    List<Object[]> findAllTranslationsFromAllergens();

    @Query("SELECT c.name FROM Category c")
    List<Translatable> findAllTranslationsFromCategories();

    @Query("SELECT i.name FROM Ingredient i")
    List<Translatable> findAllTranslationsFromIngredients();

    @Query("SELECT l.name FROM Label l")
    List<Translatable> findAllTranslationsFromLabels();

    @Query("SELECT mi.name, mi.description FROM MenuItem mi")
    List<Object[]> findAllTranslationsFromMenuItems();

    @Query("SELECT v.name FROM MenuItemVariant v")
    List<Translatable> findAllTranslationsFromVariants();

    @Query("SELECT z.name FROM Zone z")
    List<Translatable> findAllTranslationsFromZones();

}
