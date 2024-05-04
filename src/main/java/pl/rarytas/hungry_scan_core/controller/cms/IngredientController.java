package pl.rarytas.hungry_scan_core.controller.cms;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.hungry_scan_core.controller.ResponseHelper;
import pl.rarytas.hungry_scan_core.entity.Ingredient;
import pl.rarytas.hungry_scan_core.service.interfaces.IngredientService;

import java.util.List;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Ingredient>> list() {
        return ResponseEntity.ok(ingredientService.findAll());
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

    @PostMapping(value = "/add")
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

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}