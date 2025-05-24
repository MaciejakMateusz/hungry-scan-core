package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.IngredientMapper;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.IngredientRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImp implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ExceptionHelper exceptionHelper;
    private final IngredientMapper ingredientMapper;
    private final TranslatableMapper translatableMapper;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public void save(IngredientSimpleDTO ingredientDTO) throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        Ingredient ingredient = ingredientMapper.toIngredient(ingredientDTO);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        ingredient.setRestaurant(restaurant);
        ingredientRepository.save(ingredient);
    }

    @Override
    @Transactional
    public void update(IngredientSimpleDTO ingredientDTO) throws LocalizedException {
        Ingredient existing = getIngredient(ingredientDTO.id());
        Long restaurantId = userService.getActiveRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        existing.setRestaurant(restaurant);
        existing.setName(translatableMapper.toTranslatable(ingredientDTO.name()));
        existing.setPrice(ingredientDTO.price());
        existing.setAvailable(ingredientDTO.available());
        ingredientRepository.save(existing);
    }

    @Override
    public List<IngredientDTO> findAll() throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        List<Ingredient> ingredients = ingredientRepository.findAllOrderByDefaultTranslation(restaurantId);
        return ingredients.stream().map(ingredientMapper::toDTO).toList();
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