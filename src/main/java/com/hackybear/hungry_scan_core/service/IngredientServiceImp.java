package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.IngredientMapper;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.IngredientRepository;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class IngredientServiceImp implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ExceptionHelper exceptionHelper;
    private final IngredientMapper ingredientMapper;
    private final TranslatableMapper translatableMapper;
    private final UserService userService;

    public IngredientServiceImp(IngredientRepository ingredientRepository, ExceptionHelper exceptionHelper, IngredientMapper ingredientMapper, TranslatableMapper translatableMapper, UserService userService) {
        this.ingredientRepository = ingredientRepository;
        this.exceptionHelper = exceptionHelper;
        this.ingredientMapper = ingredientMapper;
        this.translatableMapper = translatableMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void save(IngredientSimpleDTO ingredientDTO) throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        Ingredient ingredient = ingredientMapper.toIngredient(ingredientDTO);
        ingredient.setRestaurantId(restaurantId);
        ingredientRepository.save(ingredient);
    }

    @Override
    @Transactional
    public void update(IngredientSimpleDTO ingredientDTO) throws LocalizedException {
        Ingredient existing = getIngredient(ingredientDTO.id());
        Long restaurantId = userService.getActiveRestaurantId();
        existing.setRestaurantId(restaurantId);
        existing.setName(translatableMapper.toTranslatable(ingredientDTO.name()));
        existing.setPrice(ingredientDTO.price());
        existing.setAvailable(ingredientDTO.available());
        ingredientRepository.save(existing);
    }

    @Override
    public List<IngredientDTO> findAll() throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        List<Ingredient> ingredients = ingredientRepository.findAllOrderByDefaultTranslation(restaurantId);
        return ingredients.stream().map(ingredientMapper::toDTO).sorted().toList();
    }

    @Override
    public Page<IngredientDTO> findAllPages(Pageable pageable) throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        return ingredientRepository.findAllOrderByDefaultTranslation(pageable, restaurantId).map(ingredientMapper::toDTO);
    }

    @Override
    public IngredientDTO findById(Long id) throws LocalizedException {
        return ingredientMapper.toDTO(getIngredient(id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        ingredientRepository.deleteById(id);
    }

    @Override
    public List<IngredientDTO> filterByName(String value) throws LocalizedException {
        String filterValue = "%" + value.toLowerCase() + "%";
        Long restaurantId = userService.getActiveRestaurantId();
        return ingredientRepository.filterByName(filterValue, restaurantId).stream().map(ingredientMapper::toDTO).toList();
    }

    private Ingredient getIngredient(Long id) throws LocalizedException {
        return ingredientRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.ingredientService.ingredientNotFound", id));
    }
}