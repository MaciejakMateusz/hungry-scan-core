package pl.rarytas.rarytas_restaurantside.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RoleService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminManagementController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminManagementController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String users(Model model, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        model.addAttribute("currentAdminId", currentAdmin.getId());
        model.addAttribute("users", getAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/waiters")
    public String waiters(Model model, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        model.addAttribute("currentAdminId", currentAdmin.getId());
        model.addAttribute("users", userService.findAllByRole("ROLE_WAITER"));
        return "admin/waiters";
    }

    @GetMapping("/cooks")
    public String cooks(Model model, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        model.addAttribute("currentAdminId", currentAdmin.getId());
        model.addAttribute("users", userService.findAllByRole("ROLE_COOK"));
        return "admin/cooks";
    }

    @GetMapping("managers")
    public String managers(Model model, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        model.addAttribute("currentAdminId", currentAdmin.getId());
        model.addAttribute("users", userService.findAllByRole("ROLE_MANAGER"));
        return "admin/managers";
    }

    @GetMapping("/admins")
    public String admins(Model model, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        model.addAttribute("currentAdminId", currentAdmin.getId());
        model.addAttribute("users", userService.findAllByRole("ROLE_ADMIN"));
        return "admin/admins";
    }

    @PostMapping("/show")
    public String show(Model model, @RequestParam Integer id) {
        model.addAttribute("user", userService.findById(id));
        return "admin/users/show";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/add";
    }

    @PostMapping("/add")
    public String add(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "admin/users/add";
        } else if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailExists", true);
            return "admin/users/add";
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            model.addAttribute("passwordsNotMatch", true);
            return "admin/users/add";
        }
        userService.save(user);
        model.addAttribute("isCreated", true);
        model.addAttribute("user", new User());
        return "admin/users/add";
    }

    @PostMapping("/edit")
    public String edit(Model model, @RequestParam Integer id) {
        model.addAttribute("user", userService.findById(id));
        return "admin/users/edit";
    }

    @PostMapping("/update")
    public String edit(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "admin/users/edit";
        }
        userService.save(user);
        model.addAttribute("isUpdated", true);
        return "admin/users/edit";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "admin/users/delete";
    }

    @PostMapping("/remove")
    public String delete(Model model, User user, Principal principal) {
        User currentAdmin = userService.findByUsername(principal.getName());
        if (currentAdmin.getId().equals(user.getId())) {
            return "admin/users/delete";
        }
        userService.delete(user);
        model.addAttribute("isRemoved", true);
        return "admin/users/delete";
    }

    @ModelAttribute("roles")
    private Set<Role> getAllRoles() {
        return roleService.findAll();
    }

    private List<User> getAllUsers() {
        return userService.findAll(PageRequest.ofSize(100)).stream().toList();
    }
}