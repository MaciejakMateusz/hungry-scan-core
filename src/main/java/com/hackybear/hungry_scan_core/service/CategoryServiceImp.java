package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TranslatableMapper translatableMapper;
    private final UserService userService;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;

    public CategoryServiceImp(CategoryRepository categoryRepository,
                              CategoryMapper categoryMapper, TranslatableMapper translatableMapper,
                              UserService userService,
                              ExceptionHelper exceptionHelper,
                              SortingHelper sortingHelper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.translatableMapper = translatableMapper;
        this.userService = userService;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
    }

    @Override
    public List<CategoryDTO> findAll() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        List<Category> categories = categoryRepository.findAllByMenuIdOrderByDisplayOrder(activeMenuId);
        return categories.stream().map(this::mapToCategoryDTO).toList();
    }

    @Override
    public List<Integer> findAllDisplayOrders() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        return categoryRepository.findAllDisplayOrdersByMenu(activeMenuId);
    }

    @Override
    public Long countAll() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        return categoryRepository.countByMenuId(activeMenuId);
    }

    @Override
    public List<CategoryCustomerDTO> findAllAvailableAndVisible() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        List<Category> categories = categoryRepository.findAllAvailableByMenuId(activeMenuId);
        return filterUnavailableMenuItems(categories);
    }

    @Override
    public CategoryFormDTO findById(Long id) throws LocalizedException {
        Category category = getCategory(id);
        return categoryMapper.toFormDTO(category);
    }

    @Transactional
    @Override
    public void save(CategoryFormDTO categoryFormDTO) throws Exception {
        Long activeMenuId = userService.getActiveMenuId();
        Category category = categoryMapper.toCategory(categoryFormDTO);
        category.setMenuId(activeMenuId);
        sortingHelper.sortAndSave(category, this::getCategory);
    }

    @Transactional
    @Override
    public void update(CategoryFormDTO categoryFormDTO) throws Exception {
        Category existingCategory = getCategory(categoryFormDTO.id());
        existingCategory.setName(translatableMapper.toTranslatable(categoryFormDTO.name()));
        existingCategory.setDisplayOrder(categoryFormDTO.displayOrder());
        existingCategory.setAvailable(categoryFormDTO.available());
        sortingHelper.sortAndSave(existingCategory, this::getCategory);
    }

    @Transactional
    @Override
    public void delete(Long id) throws LocalizedException {
        Category existingCategory = getCategory(id);
        if (!existingCategory.getMenuItems().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.categoryService.categoryNotEmpty");
            return;
        }
        sortingHelper.removeAndAdjust(existingCategory);
    }

    private Category getCategory(Long id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }

    private List<CategoryCustomerDTO> filterUnavailableMenuItems(List<Category> categories) {
        for (Category category : categories) {
            category.getMenuItems().removeIf(menuItem -> !menuItem.isVisible());
        }
        return categories.stream().map(categoryMapper::toCustomerDTO).toList();
    }

    private CategoryDTO mapToCategoryDTO(Category category) {
        return categoryMapper.toDTO(category);
    }

}