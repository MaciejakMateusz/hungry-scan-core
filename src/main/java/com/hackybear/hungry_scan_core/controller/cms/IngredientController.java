package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/cms/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> findAll() {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(ingredientService.findAll(restaurantId));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> pages(@RequestBody Map<String, Object> params) {
        Integer pageSize = (Integer) params.get("pageSize");
        Integer pageNumber = (Integer) params.get("pageNumber");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(ingredientService.findAllPages(pageable, restaurantId));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }

    }

    @PostMapping("/show")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, ingredientService::findById);
    }

    @PostMapping("/add")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> add(@RequestBody @Valid IngredientSimpleDTO ingredient, BindingResult br) {
        if (br.hasErrors()) {
            return responseHelper.createErrorResponse(br);
        }
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            ingredientService.save(ingredient, restaurantId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping("/update")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> update(@RequestBody @Valid IngredientSimpleDTO ingredient, BindingResult br) {
        if (br.hasErrors()) {
            return responseHelper.createErrorResponse(br);
        }
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            ingredientService.update(ingredient, restaurantId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Long id) {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            ingredientService.delete(id, restaurantId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/filter")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> filterByName(@RequestBody String value) {
        try {
            return ResponseEntity.ok(ingredientService.filterByName(value));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}