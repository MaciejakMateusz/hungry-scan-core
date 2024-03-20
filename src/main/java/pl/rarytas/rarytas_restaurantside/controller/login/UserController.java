package pl.rarytas.rarytas_restaurantside.controller.login;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.dto.AuthRequestDTO;
import pl.rarytas.rarytas_restaurantside.dto.JwtResponseDTO;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.*;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final RestaurantTableService restaurantTableService;
    private final SettingsService settingsService;
    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;

    public UserController(AuthenticationManager authenticationManager,
                          RestaurantTableService restaurantTableService,
                          SettingsService settingsService,
                          UserService userService,
                          RoleService roleService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.restaurantTableService = restaurantTableService;
        this.settingsService = settingsService;
        this.userService = userService;
        this.roleService = roleService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public JwtResponseDTO login(@RequestBody AuthRequestDTO authRequestDTO) {
        Settings settings = settingsService.getSettings();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            return JwtResponseDTO
                    .builder()
                    .accessToken(jwtService.generateToken(authRequestDTO.getUsername(),
                            settings.getEmployeeSessionTime())).build();
        } else {
            throw new UsernameNotFoundException("Not authorized - invalid user request");
        }
    }

    @GetMapping("/scan/{token}")
    public JwtResponseDTO scanQR(@PathVariable("token") String token) throws AccessDeniedException, LocalizedException {
        Settings settings = settingsService.getSettings();
        RestaurantTable restaurantTable = getRestaurantTable(token);
        if (restaurantTable.isActive()) {
            String randomUUID = UUID.randomUUID().toString();
            persistTableAndUser(randomUUID, restaurantTable);
            return JwtResponseDTO
                    .builder()
                    .accessToken(jwtService.generateToken(randomUUID.substring(1, 13),
                            settings.getEmployeeSessionTime())).build();
        } else {
            throw new LocalizedException("Table not activated");
        }
    }

    private void persistTableAndUser(String uuid, RestaurantTable restaurantTable) {
        String username = uuid.substring(1, 13);
        userService.save(createTempCustomer(username));
        User customer = userService.findByUsername(username);
        restaurantTable.addCustomer(customer);
        restaurantTableService.save(restaurantTable);
    }

    private RestaurantTable getRestaurantTable(String token) throws AccessDeniedException {
        RestaurantTable restaurantTable;
        try {
            restaurantTable = restaurantTableService.findByToken(token);
        } catch (LocalizedException e) {
            throw new AccessDeniedException(e.getLocalizedMessage());
        }
        return restaurantTable;
    }

    private User createTempCustomer(String username) {
        User customer = new User();
        customer.setUsername(username);
        customer.setEmail(username + "@temp.it");
        customer.setPassword(UUID.randomUUID().toString());
        Role role = roleService.findByName("ROLE_CUSTOMER");
        customer.setRoles(new HashSet<>(Collections.singletonList(role)));
        return customer;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}