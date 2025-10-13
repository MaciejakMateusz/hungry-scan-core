package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cms/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ExceptionHelper exceptionHelper;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> list() {
        ThrowingSupplier<User> supplier = userService::getCurrentUser;
        return responseHelper.buildResponse(supplier, restaurantService::findAll);
    }

    @GetMapping("/organization")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> listByOrganizationId() {
        ThrowingSupplier<User> supplier = userService::getCurrentUser;
        return responseHelper.buildResponse(supplier, restaurantService::findAllByOrganizationId);
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> currentRestaurant() {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(restaurantService.findCurrent(user));
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, restaurantService::findById);
    }

    @PostMapping("/create-first")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> createFirst(@Valid @RequestBody RestaurantDTO restaurantDTO, BindingResult br) {
        Map<String, Object> params = Map.of(
                "restaurantDTO", restaurantDTO,
                "bindingResult", br,
                "responseHelper", responseHelper);
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
            return restaurantService.persistInitialRestaurant(params, currentUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error",
                    exceptionHelper.getLocalizedMsg("error.createRestaurant.currentUser")));
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@Valid @RequestBody RestaurantDTO restaurant, BindingResult br) {
        return responseHelper.buildResponse(restaurant, br, userService::getCurrentUser, restaurantService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody RestaurantDTO restaurant, BindingResult br) {
        return responseHelper.buildResponse(restaurant, br, userService::getCurrentUser, restaurantService::update);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete() {
        try {
            User currentUser = userService.getCurrentUser();
            return responseHelper.buildResponse(currentUser, restaurantService::delete);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
