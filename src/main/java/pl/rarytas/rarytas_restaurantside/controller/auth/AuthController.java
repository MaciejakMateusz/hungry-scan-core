package pl.rarytas.rarytas_restaurantside.controller.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @GetMapping("/restaurant")
    @PreAuthorize("hasAnyRole('WAITER', 'COOK', 'MANAGER', 'ADMIN')")
    public boolean restaurantAuth() {
        return true;
    }

    @GetMapping("/cms")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public boolean cmsAuth() {
        return true;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean adminAuth() {
        return true;
    }
}