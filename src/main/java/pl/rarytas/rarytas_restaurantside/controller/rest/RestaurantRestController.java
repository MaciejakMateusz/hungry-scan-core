package pl.rarytas.rarytas_restaurantside.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.service.RestaurantService;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Slf4j
public class RestaurantRestController {

    private final RestaurantService restaurantService;
    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantService.findAll();
    }

    @GetMapping("/{id}")
    public Restaurant getById(@PathVariable Integer id) {
        return restaurantService.findById(id).orElseThrow();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
