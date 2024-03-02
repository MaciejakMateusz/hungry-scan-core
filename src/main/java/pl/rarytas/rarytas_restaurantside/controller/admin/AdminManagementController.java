package pl.rarytas.rarytas_restaurantside.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RoleService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminManagementController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

    @GetMapping("managers")
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
    public ResponseEntity<User> show(@RequestParam Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/add")
    public ResponseEntity<User> add() {
        return ResponseEntity.ok(new User());
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestParam User user, BindingResult br) {
        return buildResponse(user, br);
    }

    @PostMapping("/edit")
    public ResponseEntity<User> edit(@RequestParam Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> edit(@Valid @RequestParam User user, BindingResult br) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        userService.save(user);
        params.put("isUpdated", true);
        return ResponseEntity.ok(params);
    }

    @PostMapping("/delete")
    public ResponseEntity<User> delete(@RequestParam Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> delete(User user, Principal principal) {
        Map<String, Object> params = new HashMap<>();
        User currentAdmin = userService.findByUsername(principal.getName());
        if (currentAdmin.getId().equals(user.getId())) {
            params.put("illegalRemoval", true);
            return ResponseEntity.badRequest().body(params);
        }
        userService.delete(user);
        params.put("isRemoved", true);
        return ResponseEntity.ok(params);
    }

    @ModelAttribute("roles")
    private Set<Role> getAllRoles() {
        return roleService.findAll();
    }

    private ResponseEntity<Map<String, Object>> buildResponse(User user, BindingResult br) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        } else if (userService.existsByEmail(user.getEmail())) {
            params.put("emailExists", true);
            return ResponseEntity.badRequest().body(params);
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            params.put("passwordsNotMatch", true);
            return ResponseEntity.badRequest().body(params);
        }
        userService.save(user);
        params.put("isCreated", true);
        params.put("user", new User());
        return ResponseEntity.ok(params);
    }

    private Map<String, String> getFieldErrors(BindingResult br) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : br.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return fieldErrors;
    }
}