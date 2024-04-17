package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Ingredient;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.IngredientRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.IngredientService;

import java.util.List;

@Slf4j
@Service
public class IngredientServiceImp implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ExceptionHelper exceptionHelper;

    public IngredientServiceImp(IngredientRepository ingredientRepository, ExceptionHelper exceptionHelper) {
        this.ingredientRepository = ingredientRepository;

        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    @Override
    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public Ingredient findById(Integer id) throws LocalizedException {
        return ingredientRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.ingredientService.ingredientNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Ingredient existingIngredient = findById(id);
        ingredientRepository.delete(existingIngredient);
    }
}