package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
