package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantTables")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @GetMapping
    public List<RestaurantTable> getAll() {
        return restaurantTableService.findAll();
    }

    @GetMapping("/{id}")
    public RestaurantTable getById(@PathVariable Integer id) {
        return restaurantTableService.findById(id).orElseThrow();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
