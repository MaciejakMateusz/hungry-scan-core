package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IngredientService {

    void save(Ingredient ingredient);

    Page<Ingredient> findAll(Pageable pageable);

    Ingredient findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

}
