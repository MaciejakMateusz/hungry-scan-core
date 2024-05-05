package pl.rarytas.hungry_scan_core.controller.cms;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.hungry_scan_core.controller.ResponseHelper;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;
import pl.rarytas.hungry_scan_core.service.interfaces.FileProcessingService;
import pl.rarytas.hungry_scan_core.service.interfaces.QRService;
import pl.rarytas.hungry_scan_core.service.interfaces.RestaurantTableService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/tables")
public class TableController {

    private final RestaurantTableService restaurantTableService;
    private final FileProcessingService fileProcessingService;
    private final QRService qrService;
    private final ResponseHelper responseHelper;

    public TableController(RestaurantTableService restaurantTableService,
                           FileProcessingService fileProcessingService,
                           QRService qrService,
                           ResponseHelper responseHelper) {
        this.restaurantTableService = restaurantTableService;
        this.fileProcessingService = fileProcessingService;
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

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid RestaurantTable restaurantTable,
                                 BindingResult br) {
        return responseHelper.buildResponse(restaurantTable, br, restaurantTableService::save);
    }

    @PatchMapping("/generate-token")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateNewToken(@RequestBody Integer id) {
        return responseHelper.buildResponse(id, restaurantTableService::generateNewToken);
    }

    @PostMapping("/generate-qr")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateQr(@RequestBody Integer id) {
        try {
            RestaurantTable restaurantTable = restaurantTableService.generateNewToken(id);
            qrService.generate(restaurantTable);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping(value = "/download", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> downloadFile(@RequestBody Integer id) {
        try {
            RestaurantTable restaurantTable = restaurantTableService.findById(id);
            Resource file = fileProcessingService.downloadFile(restaurantTable.getQrName());

            String filename = file.getFilename();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok().headers(headers).body(file);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
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