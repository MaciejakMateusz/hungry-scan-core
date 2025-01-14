package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.hackybear.hungry_scan_core.utility.Fields.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ResponseHelper responseHelper;

    @Value("${CMS_APP_URL}")
    private String cmsAppUrl;

    public AuthController(UserService userService,
                          ResponseHelper responseHelper) {
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @GetMapping("/restaurant")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public boolean restaurantAuth() {
        return true;
    }

    @GetMapping("/cms")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> cmsAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.ok().build() :
                responseHelper.redirectTo(cmsAppUrl + "/create-restaurant");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.ok().build() :
                responseHelper.redirectTo(cmsAppUrl + "/create-restaurant");
    }

    @GetMapping("/create-restaurant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRestaurantAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() :
                ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}