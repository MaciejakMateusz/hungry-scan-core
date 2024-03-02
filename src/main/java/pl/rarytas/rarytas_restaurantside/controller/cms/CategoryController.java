package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> list() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/add")
    public ResponseEntity<Category> add() {
        return ResponseEntity.ok(new Category());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestParam Category category, BindingResult br) {
        return buildResponseEntity(category, br, categoryService);
    }

    @PostMapping("/edit")
    public ResponseEntity<Category> updateItem(@RequestParam Integer id) {
        return ResponseEntity.ok(categoryService.findById(id).orElseThrow());
    }

    @Modifying
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateItem(@Valid @RequestParam Category category, BindingResult br) {
        return buildResponseEntity(category, br, categoryService);
    }

    @PostMapping("/delete")
    public ResponseEntity<Category> deleteItem(@RequestParam Integer id) {
        return ResponseEntity.ok(categoryService.findById(id).orElseThrow());
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestParam Category category) {
        Map<String, Object> params = new HashMap<>();
        categoryService.delete(category);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(Category category, BindingResult br, CategoryService service) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        service.save(category);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }

    private Map<String, String> getFieldErrors(BindingResult br) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : br.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return fieldErrors;
    }
}