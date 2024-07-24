package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cms/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final ResponseHelper responseHelper;

    public IngredientController(IngredientService ingredientService, ResponseHelper responseHelper) {
        this.ingredientService = ingredientService;
        this.responseHelper = responseHelper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Page<Ingredient>> list(@RequestBody Map<String, Object> params) {
        Integer pageSize = (Integer) params.get("pageSize");
        Integer pageNumber = (Integer) params.get("pageNumber");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ResponseEntity.ok(ingredientService.findAll(pageable));
    }

    @PostMapping("/show")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, ingredientService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Ingredient> add() {
        return ResponseEntity.ok(new Ingredient());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid Ingredient ingredient,
                                 BindingResult br) {
        return responseHelper.buildResponse(ingredient, br, ingredientService::save);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, ingredientService::delete);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> filterByName(@RequestBody String name) {
        return ResponseEntity.ok(ingredientService.filterByName(name));
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}