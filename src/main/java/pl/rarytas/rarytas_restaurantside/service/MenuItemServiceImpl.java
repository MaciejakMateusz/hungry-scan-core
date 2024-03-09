package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.FileStorageService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final FileStorageService fileStorageService;
    private final ExceptionHelper exceptionHelper;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, FileStorageService fileStorageService, ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.fileStorageService = fileStorageService;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(MenuItem menuItem, MultipartFile file) {
        if(Objects.nonNull(file)) {
            try {
                fileStorageService.storeFile(file);
            } catch (IOException e) {
                log.warn(e.getLocalizedMessage());
            }
            menuItem.setImageName(file.getOriginalFilename());
        }
        menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem findById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuItemService.menuItemNotExist", id));
    }

    @Override
    public void delete(MenuItem menuItem) {
        menuItemRepository.delete(menuItem);
    }
}