package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/items")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final ResponseHelper responseHelper;

    public MenuItemController(MenuItemService menuItemService, ResponseHelper responseHelper) {
        this.menuItemService = menuItemService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> itemsList() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> updateItem(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, menuItemService::findById);
    }

    @GetMapping("/add")
    public ResponseEntity<MenuItem> addItem() {
        return ResponseEntity.ok(new MenuItem());
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Map<String, Object>> addItem(@RequestParam("file") MultipartFile file,
                                                       @RequestBody @Valid MenuItem menuItem,
                                                       BindingResult br) {
        return responseHelper.buildResponse(menuItem, file, br, menuItemService::save);
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