package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.annotation.WithRateLimitProtection;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
public class QrScanController {

    private final QRService qrService;

    public QrScanController(QRService qrService) {
        this.qrService = qrService;
    }

    @WithRateLimitProtection
    @GetMapping("/scan/{restaurantToken}")
    public ResponseEntity<?> scanQr(HttpServletResponse response, @PathVariable String restaurantToken) throws IOException {
        return qrService.scanQRCode(response, restaurantToken);
    }

    @WithRateLimitProtection
    @PostMapping("/post-scan")
    public void postScan(@RequestBody String footprint) {
        qrService.persistScanEvent(footprint);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}