package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void save(MenuItem menuItem, MultipartFile file) throws IOException {

        if (menuItem.getId() == null) {
            menuItemRepository.save(menuItem);
            return;
        }

        setImageFile(menuItem, file);
        menuItemRepository.save(menuItem);
    }

    private void setImageFile(MenuItem menuItem, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            MenuItem existingItem = menuItemRepository.findById(menuItem.getId()).orElseThrow();
            menuItem.setImage(existingItem.getImage());
        } else {
            menuItem.setImage(file.getBytes());
        }
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public Optional<MenuItem> findById(Integer id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public void delete(MenuItem menuItem) {
        menuItemRepository.delete(menuItem);
    }
}