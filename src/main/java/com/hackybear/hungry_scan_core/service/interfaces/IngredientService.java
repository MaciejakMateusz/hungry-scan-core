package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientService {

    void save(IngredientSimpleDTO ingredientDTO, Long restaurantId) throws LocalizedException;

    void update(IngredientSimpleDTO ingredientDTO, Long restaurantId) throws LocalizedException;

    List<IngredientDTO> findAll(Long restaurantId);

    Page<IngredientDTO> findAllPages(Pageable pageable, Long restaurantId);

    IngredientDTO findById(Long id) throws LocalizedException;

    void delete(Long id, Long restaurantId);

    List<IngredientDTO> filterByName(String name) throws LocalizedException;

}
