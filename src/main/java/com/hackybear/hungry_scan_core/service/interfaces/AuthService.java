package com.hackybear.hungry_scan_core.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> authorizeApp();

    ResponseEntity<?> authorizeCreateRestaurant();

}
