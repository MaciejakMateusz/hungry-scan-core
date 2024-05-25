package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Translatable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslatableRepository extends JpaRepository<Translatable, Integer> {

    @Query("SELECT c.name FROM Category c")
    List<Translatable> findAllTranslationsFromCategories();

    @Query("SELECT mi.name, mi.description FROM MenuItem mi")
    List<Object[]> findAllTranslationsFromMenuItems();

}
