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
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TranslatableMapper translatableMapper;
    private final UserService userService;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final EntityManager entityManager;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;

    public CategoryServiceImp(CategoryRepository categoryRepository,
                              CategoryMapper categoryMapper,
                              TranslatableMapper translatableMapper,
                              UserService userService,
                              ExceptionHelper exceptionHelper,
                              SortingHelper sortingHelper,
                              EntityManager entityManager, MenuItemRepository menuItemRepository, VariantRepository variantRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.translatableMapper = translatableMapper;
        this.userService = userService;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
        this.entityManager = entityManager;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
    }

    @Override
    @Cacheable("allCategories")
    public List<CategoryDTO> findAll() throws LocalizedException {
        return getAllCategories();
    }

    @Override
    @Cacheable("allDisplayOrders")
    public List<Integer> findAllDisplayOrders() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        return categoryRepository.findAllDisplayOrdersByMenuId(activeMenuId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "allCategories", allEntries = true)
    public List<CategoryDTO> updateDisplayOrders(List<CategoryFormDTO> categoryDTOs) throws LocalizedException {
        List<Category> categories = categoryDTOs.stream().map(categoryMapper::toCategory).toList();
        for (Category category : categories) {
            categoryRepository.updateDisplayOrders(category.getId(), category.getDisplayOrder());
        }
        entityManager.clear();
        return getAllCategories();
    }

    @Override
    @Cacheable("categoryCount")
    public Long countAll() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        return categoryRepository.countByMenuId(activeMenuId);
    }

    @Override
    @Cacheable("availableAndVisibleCategories")
    public List<CategoryCustomerDTO> findAllAvailableAndVisible() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        List<Category> categories = categoryRepository.findAllAvailableByMenuId(activeMenuId);
        return filterUnavailableMenuItems(categories);
    }

    @Override
    @Cacheable(value = "categoryById", key = "#id")
    public CategoryFormDTO findById(Long id) throws LocalizedException {
        Category category = getCategory(id);
        return categoryMapper.toFormDTO(category);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"allCategories", "allDisplayOrders", "categoryCount", "availableAndVisibleCategories"}, allEntries = true)
    public void save(CategoryFormDTO categoryFormDTO) throws Exception {
        Long activeMenuId = userService.getActiveMenuId();
        Category category = categoryMapper.toCategory(categoryFormDTO);
        category.setMenuId(activeMenuId);
        Optional<Integer> maxDisplayOrder = categoryRepository.findMaxDisplayOrderByMenuId(activeMenuId);
        category.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        categoryRepository.save(category);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"allCategories", "allDisplayOrders", "categoryById", "availableAndVisibleCategories"}, allEntries = true)
    public void update(CategoryFormDTO categoryFormDTO) throws Exception {
        Category existingCategory = getCategory(categoryFormDTO.id());
        existingCategory.setName(translatableMapper.toTranslatable(categoryFormDTO.name()));
        existingCategory.setAvailable(categoryFormDTO.available());
        categoryRepository.saveAndFlush(existingCategory);
    }

    @Transactional
    @Override
    @CacheEvict(value = {"allCategories", "allDisplayOrders", "categoryCount", "availableAndVisibleCategories"}, allEntries = true)
    public List<CategoryDTO> delete(Long id) throws LocalizedException {
        Category existingCategory = getCategory(id);
        if (!existingCategory.getMenuItems().isEmpty()) {
            cascadeRemoveMenuItems(existingCategory);
        }
        categoryRepository.deleteById(id);
        List<Category> categories = categoryRepository.findAllByMenuIdOrderByDisplayOrder(existingCategory.getMenuId());
        sortingHelper.reassignDisplayOrders(categories, categoryRepository::saveAllAndFlush);
        return getAllCategories();
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
        List<MenuItem> menuItems = category.getMenuItems();
        for (MenuItem menuItem : menuItems) {
            variantRepository.deleteAll(menuItem.getVariants());
        }
        menuItemRepository.deleteAll(menuItems);
    }

    private List<CategoryDTO> getAllCategories() throws LocalizedException {
        Long activeMenuId = userService.getActiveMenuId();
        List<Category> categories = categoryRepository.findAllByMenuIdOrderByDisplayOrder(activeMenuId);
        return categories.stream().sorted().map(this::mapToCategoryDTO).toList();
    }
}