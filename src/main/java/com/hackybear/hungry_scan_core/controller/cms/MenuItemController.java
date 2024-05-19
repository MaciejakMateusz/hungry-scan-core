package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final ResponseHelper responseHelper;

    public MenuItemController(MenuItemService menuItemService,
                              ResponseHelper responseHelper) {
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

    @PostMapping("/by-category")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MenuItem>> findAllByCategoryId(@RequestBody Integer id) {
        return ResponseEntity.ok(menuItemService.findAllByCategoryId(id));
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<MenuItem> add() {
        return ResponseEntity.ok(new MenuItem());
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid MenuItem menuItem,
                                 BindingResult br) {
        return responseHelper.buildResponse(menuItem, br, menuItemService::save);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, menuItemService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}