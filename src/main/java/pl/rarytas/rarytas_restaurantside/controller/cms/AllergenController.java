package pl.rarytas.rarytas_restaurantside.controller.cms;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Allergen;
import pl.rarytas.rarytas_restaurantside.service.interfaces.AllergenService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/allergens")
public class AllergenController {

    private final AllergenService allergenService;
    private final ResponseHelper responseHelper;

    public AllergenController(AllergenService allergenService, ResponseHelper responseHelper) {
        this.allergenService = allergenService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Allergen>> list() {
        return ResponseEntity.ok(allergenService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, allergenService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}