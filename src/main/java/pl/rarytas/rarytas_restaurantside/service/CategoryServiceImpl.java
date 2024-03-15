package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.CategoryRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExceptionHelper exceptionHelper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ExceptionHelper exceptionHelper) {
        this.categoryRepository = categoryRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Integer id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void delete(Category category) {
        categoryRepository.delete(category);
    }
}
