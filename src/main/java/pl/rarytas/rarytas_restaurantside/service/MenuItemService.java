package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemServiceInterface;

import java.io.IOException;

@Slf4j
@Service
public class MenuItemService implements MenuItemServiceInterface {
    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void save(MenuItem menuItem, MultipartFile file) throws IOException {

        if (menuItem.getId() == null) {
            menuItemRepository.save(menuItem);
            return;
        }

        if (file.isEmpty()) {
            MenuItem existingItem = menuItemRepository.findById(menuItem.getId()).orElseThrow();
            menuItem.setImage(existingItem.getImage());
        } else {
            menuItem.setImage(file.getBytes());
        }
        menuItemRepository.save(menuItem);
    }
}