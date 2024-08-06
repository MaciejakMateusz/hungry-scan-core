package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientService {

    void save(Ingredient ingredient);

    List<Ingredient> findAll();

    Page<Ingredient> findAllPages(Pageable pageable);

    Ingredient findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

    List<Ingredient> filterByName(String name);

}
