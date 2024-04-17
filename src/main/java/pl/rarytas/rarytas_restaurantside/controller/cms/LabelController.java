package pl.rarytas.rarytas_restaurantside.controller.cms;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Label;
import pl.rarytas.rarytas_restaurantside.service.interfaces.LabelService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/labels")
public class LabelController {

    private final LabelService labelService;
    private final ResponseHelper responseHelper;

    public LabelController(LabelService labelService, ResponseHelper responseHelper) {
        this.labelService = labelService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Label>> list() {
        return ResponseEntity.ok(labelService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, labelService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}