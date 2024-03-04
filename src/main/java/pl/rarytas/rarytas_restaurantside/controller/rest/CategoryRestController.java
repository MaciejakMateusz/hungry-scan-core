package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.List;

/**
 * GET
 * /api/categories - returns all categories - contains associated dishes
 * /api/categories/{id} - returns category with given id - contains associated dishes
 **/

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Integer id) throws LocalizedException {
        return categoryService.findById(id);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
