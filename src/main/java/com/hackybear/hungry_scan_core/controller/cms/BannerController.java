package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.BannerDTO;
import com.hackybear.hungry_scan_core.service.interfaces.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BannerDTO>> list() {
        return ResponseEntity.ok(bannerService.findAll());
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody String id) {
        return responseHelper.getResponseEntity(id, bannerService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}