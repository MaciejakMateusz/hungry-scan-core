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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

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
    @Caching(evict = {
            @CacheEvict(value = INGREDIENTS_ALL, key = "#restaurantId"),
            @CacheEvict(value = INGREDIENTS_PAGES, key = "#restaurantId"),
    })
    public void save(IngredientSimpleDTO ingredientDTO, Long restaurantId) throws LocalizedException {
        Ingredient ingredient = ingredientMapper.toIngredient(ingredientDTO);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        ingredient.setRestaurant(restaurant);
        ingredientRepository.save(ingredient);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = INGREDIENTS_ALL, key = "#restaurantId"),
            @CacheEvict(value = INGREDIENTS_PAGES, key = "#restaurantId"),
            @CacheEvict(value = INGREDIENT_ID, key = "#ingredientDTO.id()")
    })
    public void update(IngredientSimpleDTO ingredientDTO, Long restaurantId) throws LocalizedException {
        Ingredient existing = getIngredient(ingredientDTO.id());
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        existing.setRestaurant(restaurant);
        existing.setName(translatableMapper.toTranslatable(ingredientDTO.name()));
        existing.setPrice(ingredientDTO.price());
        existing.setAvailable(ingredientDTO.available());
        ingredientRepository.save(existing);
    }

    @Override
    @Cacheable(value = INGREDIENTS_ALL, key = "#restaurantId")
    public List<IngredientDTO> findAll(Long restaurantId) {
        List<Ingredient> ingredients = ingredientRepository.findAllOrderByDefaultTranslation(restaurantId);
        return ingredients.stream().map(ingredientMapper::toDTO).toList();
    }

    @Override
    @Cacheable(value = INGREDIENTS_PAGES, key = "#restaurantId")
    public Page<IngredientDTO> findAllPages(Pageable pageable, Long restaurantId) {
        return ingredientRepository.findAllOrderByDefaultTranslation(pageable, restaurantId).map(ingredientMapper::toDTO);
    }

    @Override
    @Cacheable(value = INGREDIENT_ID, key = "#id")
    public IngredientDTO findById(Long id) throws LocalizedException {
        return ingredientMapper.toDTO(getIngredient(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = INGREDIENTS_ALL, key = "#restaurantId"),
            @CacheEvict(value = INGREDIENTS_PAGES, key = "#restaurantId"),
            @CacheEvict(value = INGREDIENT_ID, key = "#id")
    })
    public void delete(Long id, Long restaurantId) {
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