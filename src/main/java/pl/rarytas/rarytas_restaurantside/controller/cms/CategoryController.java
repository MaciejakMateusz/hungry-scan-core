package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> updateItem(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, categoryService::findById);
    }

    @GetMapping("/add")
    public ResponseEntity<Category> add() {
        return ResponseEntity.ok(new Category());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestBody Category category, BindingResult br) {
        return responseHelper.buildResponse(category, br, categoryService::save);
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestBody Category category) {
        Map<String, Object> params = new HashMap<>();
        categoryService.delete(category);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }
}