package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/tables")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;
    private final ResponseHelper responseHelper;

    public RestaurantTableController(RestaurantTableService restaurantTableService, ResponseHelper responseHelper) {
        this.restaurantTableService = restaurantTableService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAll() {
        return ResponseEntity.ok(restaurantTableService.findAll());
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::findById);
    }

    @PatchMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleActivation(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::toggleActivation);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}