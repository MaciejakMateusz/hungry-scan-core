package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import java.util.List;

/**
 * GET
 * /api/items/{id} - returns dish with given id
 * /api/items - returns all dishes (list)
 **/

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:8080")
public class MenuItemRestController {

    private final MenuItemRepository menuItemRepository;

    public MenuItemRestController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }


    @GetMapping
    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public MenuItem getItem(@PathVariable Integer id) {
        return menuItemRepository.findById(id).orElseThrow();
    }

}
