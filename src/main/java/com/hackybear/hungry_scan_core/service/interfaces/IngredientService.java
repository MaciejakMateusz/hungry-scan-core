package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientService {

    void save(IngredientSimpleDTO ingredientDTO) throws LocalizedException;

    void update(IngredientSimpleDTO ingredientDTO) throws LocalizedException;

    List<IngredientDTO> findAll() throws LocalizedException;

    Page<IngredientDTO> findAllPages(Pageable pageable) throws LocalizedException;

    IngredientDTO findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

    List<IngredientDTO> filterByName(String name) throws LocalizedException;

}
