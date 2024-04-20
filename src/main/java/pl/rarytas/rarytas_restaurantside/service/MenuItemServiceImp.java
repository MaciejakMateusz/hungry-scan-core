package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.CategoryRepository;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.List;

@Slf4j
@Service
public class MenuItemServiceImp implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final ExceptionHelper exceptionHelper;
    private final CategoryRepository categoryRepository;

    public MenuItemServiceImp(MenuItemRepository menuItemRepository, ExceptionHelper exceptionHelper, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.exceptionHelper = exceptionHelper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void save(MenuItem menuItem) {
        menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem findById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void changeCategory(Integer itemId, Integer categoryId) throws LocalizedException {
        MenuItem item = findById(itemId);
        Category category = getCategoryByMenuItem(item);
        category.removeMenuItem(item);
        categoryRepository.save(category);
        Category newCategory = getCategoryById(categoryId);
        newCategory.addMenuItem(item);
        categoryRepository.save(newCategory);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        MenuItem existingMenuItem = findById(id);
        menuItemRepository.delete(existingMenuItem);
    }

    private Category getCategoryByMenuItem(MenuItem menuItem) throws LocalizedException {
        return categoryRepository.findByMenuItem(menuItem)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.categoryNotFound", menuItem.getId()));
    }

    private Category getCategoryById(Integer id) throws LocalizedException {
        return categoryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.categoryService.categoryNotFound", id));
    }
}