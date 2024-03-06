package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/items")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final ResponseHelper responseHelper;

    public MenuItemController(MenuItemService menuItemService, CategoryService categoryService, ResponseHelper responseHelper) {
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<Category>> itemsList() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping("/show")
    public ResponseEntity<MenuItem> updateItem(@RequestBody Integer id) {
        return ResponseEntity.ok(menuItemService.findById(id).orElseThrow());
    }

    @GetMapping("/add")
    public ResponseEntity<MenuItem> addItem() {
        return ResponseEntity.ok(new MenuItem());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addItem(@Valid @RequestBody MenuItem menuItem,
                                                       BindingResult br, MultipartFile imageFile) {
        return responseHelper.buildResponse(menuItem, imageFile, br, menuItemService::save);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateItem(@Valid @RequestBody MenuItem menuItem,
                                                          BindingResult br, MultipartFile imageFile) {
        return responseHelper.buildResponse(menuItem, imageFile, br, menuItemService::save);
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestBody MenuItem menuItem) {
        Map<String, Object> params = new HashMap<>();
        menuItemService.delete(menuItem);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }
}