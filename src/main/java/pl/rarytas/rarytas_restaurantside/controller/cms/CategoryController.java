package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.GenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final GenericService<Category> genericService;

    public CategoryController(CategoryService categoryService, GenericService<Category> genericService) {
        this.categoryService = categoryService;
        this.genericService = genericService;
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
    public ResponseEntity<Map<String, Object>> add(@Valid Category category, BindingResult br) {
        return buildResponseEntity(category, br, genericService);
    }

    @PostMapping("/edit")
    public ResponseEntity<Category> updateItem(@RequestParam Integer id) {
        return ResponseEntity.ok(categoryService.findById(id).orElseThrow());
    }

    @Modifying
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateItem(@Valid Category category, BindingResult br) {
        return buildResponseEntity(category, br, genericService);
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

    private <T> ResponseEntity<Map<String, Object>> buildResponseEntity(T object, BindingResult br, GenericService<T> service) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        service.save(object);
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
