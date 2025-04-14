package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.service.interfaces.PricePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cms/price-plans")
@RequiredArgsConstructor
public class PricePlanController {

    private final PricePlanService pricePlanService;
    private final ResponseHelper responseHelper;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(pricePlanService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> getById(@RequestBody String id) {
        return responseHelper.getResponseEntity(id, pricePlanService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
