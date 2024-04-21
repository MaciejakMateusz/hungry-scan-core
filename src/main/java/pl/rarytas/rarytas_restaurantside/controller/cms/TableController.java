package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.QRService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/tables")
public class TableController {

    private final RestaurantTableService restaurantTableService;
    private final QRService qrService;
    private final ResponseHelper responseHelper;

    public TableController(RestaurantTableService restaurantTableService, QRService qrService, ResponseHelper responseHelper) {
        this.restaurantTableService = restaurantTableService;
        this.qrService = qrService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<RestaurantTable>> list() {
        return ResponseEntity.ok(restaurantTableService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<RestaurantTable> add() {
        return ResponseEntity.ok(new RestaurantTable());
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid RestaurantTable restaurantTable,
                                 BindingResult br) {
        return responseHelper.buildResponse(restaurantTable, br, restaurantTableService::save);
    }

    @PatchMapping(value = "/change-zone")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> changeZone(@RequestParam Integer tableId, @RequestParam Integer zoneId) {
        try {
            restaurantTableService.changeZone(tableId, zoneId);
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/generate-token")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateNewToken(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, restaurantTableService::generateNewToken);
    }

    @PostMapping(value = "/generate-qr")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateQr(@RequestBody RestaurantTable restaurantTable) throws IOException {
        File file;
        try {
            file = qrService.generate(restaurantTable);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr-code.png");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, restaurantTableService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}