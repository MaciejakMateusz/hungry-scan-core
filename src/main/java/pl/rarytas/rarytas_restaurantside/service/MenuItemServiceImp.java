package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.List;

@Slf4j
@Service
public class MenuItemServiceImp implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final ExceptionHelper exceptionHelper;

    public MenuItemServiceImp(MenuItemRepository menuItemRepository,
                              ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<MenuItem> findAllByCategoryId(Integer id) {
        return menuItemRepository.findAllByCategoryId(id);
    }

    @Override
    public MenuItem findById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void save(MenuItem menuItem) {
        menuItemRepository.save(menuItem);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        MenuItem existingMenuItem = findById(id);
        menuItemRepository.delete(existingMenuItem);
    }

}