package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;

    public CategoryServiceImp(CategoryRepository categoryRepository, ExceptionHelper exceptionHelper, SortingHelper sortingHelper) {
        this.categoryRepository = categoryRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByDisplayOrder();
    }

    @Override
    public List<Integer> findAllDisplayOrders() {
        return categoryRepository.findAllDisplayOrders();
    }

    @Override
    public Long countAll() {
        return categoryRepository.count();
    }

    @Override
    public List<Category> findAllAvailable() {
        List<Category> categories = categoryRepository.findAllAvailable();
        return filterUnavailableMenuItems(categories);
    }

    @Override
    public Category findById(Integer id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    @Transactional
    @Override
    public void save(Category category) throws Exception {
        sortingHelper.sortAndSave(category, this::findById);
    }

    @Transactional
    @Override
    public void delete(Integer id) throws LocalizedException {
        Category existingCategory = findById(id);
        if (!existingCategory.getMenuItems().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.categoryService.categoryNotEmpty");
            return;
        }
        categoryRepository.delete(existingCategory);
        sortingHelper.updateDisplayOrders(existingCategory.getDisplayOrder(), findAll(), categoryRepository::saveAll);
    }

    private List<Category> filterUnavailableMenuItems(List<Category> categories) {
        for (Category category : categories) {
            category.getMenuItems().removeIf(menuItem -> !menuItem.isAvailable());
        }
        return categories;
    }

}