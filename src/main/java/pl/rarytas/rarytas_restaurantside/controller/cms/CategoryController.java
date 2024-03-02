package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/categories")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;
    private final ResponseHelper responseHelper;

    public CategoryController(CategoryService categoryService, ResponseHelper responseHelper) {
        this.categoryService = categoryService;
        this.responseHelper = responseHelper;
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
        return responseHelper.buildResponseEntity(category, br, categoryService::save);
    }

    @PostMapping("/edit")
    public ResponseEntity<Category> updateItem(@RequestParam Integer id) {
        return ResponseEntity.ok(categoryService.findById(id).orElseThrow());
    }

    @Modifying
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateItem(@Valid @RequestParam Category category, BindingResult br) {
        return responseHelper.buildResponseEntity(category, br, categoryService::save);
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
}