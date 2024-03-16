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
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final ResponseHelper responseHelper;

    public MenuItemController(MenuItemService menuItemService, ResponseHelper responseHelper) {
        this.menuItemService = menuItemService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MenuItem>> list() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, menuItemService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<MenuItem> add() {
        return ResponseEntity.ok(new MenuItem());
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> add(@RequestParam(required = false) MultipartFile file,
                                                   @RequestBody @Valid MenuItem menuItem,
                                                   BindingResult br) {
        return responseHelper.buildResponse(menuItem, file, br, menuItemService::save);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> remove(@RequestBody MenuItem menuItem) {
        menuItemService.delete(menuItem);
        return ResponseEntity.ok(new HashMap<>());
    }
}