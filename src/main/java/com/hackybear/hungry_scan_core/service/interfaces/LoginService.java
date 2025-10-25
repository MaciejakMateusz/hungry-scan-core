package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface LoginService {

    ResponseEntity<?> handleLogin(AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException;

    @Transactional
    ResponseEntity<?> handleLogout(HttpServletResponse response, String username);
}
