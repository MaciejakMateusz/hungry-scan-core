package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExceptionHelper exceptionHelper;

    public CategoryServiceImp(CategoryRepository categoryRepository, ExceptionHelper exceptionHelper) {
        this.categoryRepository = categoryRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllAvailable() {
        return categoryRepository.findAllAvailable();
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
    public void delete(Integer id) throws LocalizedException {
        Category existingCategory = findById(id);
        categoryRepository.delete(existingCategory);
    }
}
