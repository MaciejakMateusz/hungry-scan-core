package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Zone;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ZoneService;
import pl.rarytas.rarytas_restaurantside.utility.Constants;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/zones")
public class ZoneController {

    private final ZoneService zoneService;
    private final ResponseHelper responseHelper;

    public ZoneController(ZoneService zoneService, ResponseHelper responseHelper) {
        this.zoneService = zoneService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<List<Zone>> list() {
        return ResponseEntity.ok(zoneService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, zoneService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Zone> add() {
        return ResponseEntity.ok(new Zone());
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid Zone zone,
                                 BindingResult br) {
        return responseHelper.buildResponse(zone, br, zoneService::save);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, zoneService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}