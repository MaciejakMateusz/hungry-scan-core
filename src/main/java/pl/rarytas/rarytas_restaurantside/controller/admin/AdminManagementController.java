package pl.rarytas.rarytas_restaurantside.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final UserService userService;
    private final ResponseHelper responseHelper;

    public AdminManagementController(UserService userService,
                                     ResponseHelper responseHelper) {
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<User>> users() {
        List<User> users = userService.findAll(PageRequest.ofSize(100)).stream().toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/waiters")
    public ResponseEntity<List<User>> waiters() {
        List<User> users = userService.findAllByRole("ROLE_WAITER");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/cooks")
    public ResponseEntity<List<User>> cooks() {
        List<User> users = userService.findAllByRole("ROLE_COOK");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> managers() {
        List<User> users = userService.findAllByRole("ROLE_MANAGER");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> admins() {
        List<User> users = userService.findAllByRole("ROLE_ADMIN");
        return ResponseEntity.ok(users);
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, userService::findById);
    }

    @GetMapping("/add")
    public ResponseEntity<User> add() {
        return ResponseEntity.ok(new User());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestBody User user, BindingResult br) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            return badRequestWithErrors(br);
        } else if (userService.existsByUsername(user.getUsername())) {
            return badRequestWithParam("usernameExists");
        } else if (userService.existsByEmail(user.getEmail())) {
            return badRequestWithParam("emailExists");
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            return badRequestWithParam("passwordsNotMatch");
        }
        userService.save(user);
        return ResponseEntity.ok(params);
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody User user, BindingResult br) throws LocalizedException {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            return badRequestWithErrors(br);
        }
        if (userService.isModifiedUserValid(user)) {
            userService.save(user);
            return ResponseEntity.ok(params);
        } else {
            return badRequestWithParam(userService.getErrorParam(user));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody User user, Principal principal) {
        Map<String, Object> params = new HashMap<>();
        User currentAdmin = userService.findByUsername(principal.getName());
        if (currentAdmin.getId().equals(user.getId())) {
            params.put("illegalRemoval", true);
            return ResponseEntity.badRequest().body(params);
        }
        return responseHelper.buildResponse(user.getId(), userService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> badRequestWithErrors(BindingResult br) {
        Map<String, Object> params = new HashMap<>();
        params.put("errors", responseHelper.getFieldErrors(br));
        return ResponseEntity.badRequest().body(params);
    }

    private ResponseEntity<Map<String, Object>> badRequestWithParam(String paramName) {
        Map<String, Object> params = new HashMap<>();
        params.put(paramName, true);
        return ResponseEntity.badRequest().body(params);
    }
}