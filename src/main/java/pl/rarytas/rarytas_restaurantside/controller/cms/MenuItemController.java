package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/restaurant/cms/items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;

    public MenuItemController(MenuItemService menuItemService, CategoryService categoryService) {
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> itemsList() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/add")
    public ResponseEntity<MenuItem> addItem() {
        return ResponseEntity.ok(new MenuItem());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addItem(@Valid @RequestBody Map<String, Object> mappedRequest,
                                                       BindingResult br) {
        return buildResponseEntity(mappedRequest, br, menuItemService);
    }

    @PostMapping("/edit")
    public ResponseEntity<MenuItem> updateItem(@RequestParam Integer id) {
        return ResponseEntity.ok(menuItemService.findById(id).orElseThrow());
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateItem(@Valid @RequestBody Map<String, Object> mappedRequest,
                                                          BindingResult br) {
        return buildResponseEntity(mappedRequest, br, menuItemService);
    }

    @PostMapping("/delete")
    public ResponseEntity<MenuItem> deleteItem(@RequestParam Integer id) {
        return ResponseEntity.ok(menuItemService.findById(id).orElseThrow());
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestParam MenuItem menuItem) {
        Map<String, Object> params = new HashMap<>();
        menuItemService.delete(menuItem);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(Map<String, Object> object,
                                                                    BindingResult br,
                                                                    MenuItemService service) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        MenuItem menuItem = (MenuItem) object.get("menuItem");
        MultipartFile imageFile = (MultipartFile) object.get("imageFile");
        service.save(menuItem, imageFile);
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