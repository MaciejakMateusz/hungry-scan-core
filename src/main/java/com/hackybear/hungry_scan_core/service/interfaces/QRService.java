package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface QRService {

    void generate() throws Exception;

    void generate(RestaurantTable table, String name) throws Exception;

    ResponseEntity<?> scanQRCode(HttpServletResponse response, String restaurantToken) throws IOException;

    ResponseEntity<?> persistScanEvent(String footprint);
}
