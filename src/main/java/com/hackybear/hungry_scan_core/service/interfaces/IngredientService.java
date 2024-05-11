package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface IngredientService {

    void save(Ingredient ingredient);

    List<Ingredient> findAll();

    Ingredient findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

}
