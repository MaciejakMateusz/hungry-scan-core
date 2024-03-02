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
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final ResponseHelper responseHelper;

    public RestaurantController(RestaurantService restaurantService, ResponseHelper responseHelper) {
        this.restaurantService = restaurantService;
        this.responseHelper = responseHelper;
    }


    @GetMapping
    public ResponseEntity<List<Restaurant>> restaurantsList() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/add")
    public ResponseEntity<Restaurant> addRestaurant() {
        return ResponseEntity.ok(new Restaurant());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addRestaurant(@Valid @RequestParam Restaurant restaurant,
                                                             BindingResult br) {
        return responseHelper.buildResponseEntity(restaurant, br, restaurantService::save);
    }

    @PostMapping("/edit")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestParam Integer id) {
        return ResponseEntity.ok(restaurantService.findById(id).orElseThrow());
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateRestaurant(@Valid @RequestParam Restaurant restaurant,
                                                                BindingResult br) {
        return responseHelper.buildResponseEntity(restaurant, br, restaurantService::save);
    }

    @PostMapping("/delete")
    public ResponseEntity<Restaurant> deleteItem(@RequestParam Integer id) {
        return ResponseEntity.ok(restaurantService.findById(id).orElseThrow());
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestParam Restaurant restaurant) {
        Map<String, Object> params = new HashMap<>();
        restaurantService.delete(restaurant);
        params.put("success", true);
        return ResponseEntity.ok(params);
    }
}
