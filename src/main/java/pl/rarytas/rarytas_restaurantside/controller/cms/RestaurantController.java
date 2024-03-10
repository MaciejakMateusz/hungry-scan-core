package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ResponseHelper responseHelper;

    public RestaurantController(RestaurantService restaurantService, ResponseHelper responseHelper) {
        this.restaurantService = restaurantService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Restaurant>> list() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, restaurantService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Restaurant> add() {
        return ResponseEntity.ok(new Restaurant());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestBody Restaurant restaurant,
                                                             BindingResult br) {
        return responseHelper.buildResponse(restaurant, br, restaurantService::save);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> remove(@RequestBody Restaurant restaurant) {
        restaurantService.delete(restaurant);
        return ResponseEntity.ok(new HashMap<>());
    }
}
