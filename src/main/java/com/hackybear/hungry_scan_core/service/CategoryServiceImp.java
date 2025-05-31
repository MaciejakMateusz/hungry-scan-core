package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.CategoryMapper;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TranslatableMapper translatableMapper;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final MenuRepository menuRepository;
    private final S3Service s3Service;

    private static final String S3_PATH = "menuItems";

    @Override
    @Cacheable(value = CATEGORIES_ALL, key = "#activeMenuId")
    public List<CategoryDTO> findAll(Long activeMenuId) throws LocalizedException {
        return getAllCategories(activeMenuId);
    }

    @Override
    @Cacheable(value = CATEGORIES_DISPLAY_ORDERS, key = "#activeMenuId")
    public List<Integer> findAllDisplayOrders(Long activeMenuId) {
        return categoryRepository.findAllDisplayOrdersByMenuId(activeMenuId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {
            CATEGORIES_ALL,
            CATEGORIES_DISPLAY_ORDERS},
            key = "#activeMenuId")
    public void updateDisplayOrders(List<CategoryFormDTO> categoryDTOs, Long activeMenuId) throws LocalizedException {
        List<Category> categories = categoryDTOs.stream().map(categoryMapper::toCategory).toList();
        for (Category category : categories) {
            categoryRepository.updateDisplayOrders(category.getId(), category.getDisplayOrder());
        }
    }

    @Override
    @Cacheable(value = CATEGORIES_COUNT, key = "#activeMenuId")
    public Long countAll(Long activeMenuId) throws LocalizedException {
        return categoryRepository.countByMenuId(activeMenuId);
    }

    @Override
    @Cacheable(value = CATEGORIES_AVAILABLE, key = "#activeMenuId")
    public List<CategoryCustomerDTO> findAllAvailableAndVisible(Long activeMenuId) {
        List<Category> categories = categoryRepository.findAllAvailableByMenuId(activeMenuId);
        return filterUnavailableMenuItems(categories);
    }

    @Override
    @Cacheable(value = CATEGORY_ID, key = "#id")
    public CategoryFormDTO findById(Long id) throws LocalizedException {
        Category category = getCategory(id);
        return categoryMapper.toFormDTO(category);
    }

    @Transactional
    @Override
    @CacheEvict(value = {
            CATEGORIES_ALL,
            CATEGORIES_AVAILABLE,
            CATEGORIES_DISPLAY_ORDERS,
            CATEGORIES_COUNT},
            key = "#activeMenuId")
    public void save(CategoryFormDTO categoryFormDTO, Long activeMenuId) throws Exception {
        Category category = categoryMapper.toCategory(categoryFormDTO);
        category.setMenu(menuRepository.findById(activeMenuId).orElseThrow());
        Optional<Integer> maxDisplayOrder = categoryRepository.findMaxDisplayOrderByMenuId(activeMenuId);
        category.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        categoryRepository.save(category);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_DISPLAY_ORDERS, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#categoryFormDTO.id()")
    })
    public void update(CategoryFormDTO categoryFormDTO, Long activeMenuId) throws Exception {
        Category existingCategory = getCategory(categoryFormDTO.id());
        existingCategory.setName(translatableMapper.toTranslatable(categoryFormDTO.name()));
        existingCategory.setAvailable(categoryFormDTO.available());
        categoryRepository.saveAndFlush(existingCategory);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_DISPLAY_ORDERS, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORIES_COUNT, key = "#activeMenuId"),
            @CacheEvict(value = CATEGORY_ID, key = "#id")
    })
    public void delete(Long id, Long activeMenuId) throws LocalizedException {
        Category existingCategory = getCategory(id);
        Set<MenuItem> menuItems = existingCategory.getMenuItems();
        if (!menuItems.isEmpty()) cascadeRemoveMenuItems(existingCategory);
        categoryRepository.deleteById(id);
        Set<Category> categories = categoryRepository.findAllByMenuId(existingCategory.getMenu().getId());
        sortingHelper.reassignDisplayOrders(categories, categoryRepository::saveAllAndFlush);
        List<Long> menuItemIds = menuItems.stream().map(MenuItem::getId).toList();
        if (!menuItems.isEmpty()) s3Service.deleteAllFiles(S3_PATH, menuItemIds);
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

    private void cascadeRemoveMenuItems(Category category) {
        Set<MenuItem> menuItems = category.getMenuItems();
        for (MenuItem menuItem : menuItems) {
            variantRepository.deleteAll(menuItem.getVariants());
        }
        menuItemRepository.deleteAll(menuItems);
    }

    private List<CategoryDTO> getAllCategories(Long activeMenuId) {
        Set<Category> categories = categoryRepository.findAllByMenuId(activeMenuId);
        return categories.stream().sorted().map(this::mapToCategoryDTO).toList();
    }
}