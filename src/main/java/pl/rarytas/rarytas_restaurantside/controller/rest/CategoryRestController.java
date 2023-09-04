package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.repository.CategoryRepository;

import java.util.List;

/**
 * GET
 * /api/categories - returns all categories - contains associated dishes
 * /api/categories/{id} - returns category with given id - contains associated dishes
 **/

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:8080")
public class CategoryRestController {

    private final CategoryRepository categoryRepository;

    public CategoryRestController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Integer id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
