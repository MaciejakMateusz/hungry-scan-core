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
    public String users() {
        return "admin/dashboard";
    }

    @PostMapping("/show")
    public String show(Model model, @RequestParam Integer id) {
        model.addAttribute("user", userService.findById(id));
        return "admin/user/show";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/add";
    }

    @PostMapping("/add")
    public String add(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "admin/user/add";
        } else if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailExists", true);
            return "admin/user/add";
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            model.addAttribute("passwordsNotMatch", true);
            return "admin/user/add";
        }
        userService.save(user);
        model.addAttribute("isCreated", true);
        model.addAttribute("user", new User());
        return "admin/user/add";
    }

    @PostMapping("/edit")
    public String edit(Model model, @RequestParam Integer id) {
        model.addAttribute("user", userService.findById(id));
        return "admin/user/edit";
    }

    @PostMapping("/update")
    public String edit(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "admin/user/edit";
        }
        userService.save(user);
        model.addAttribute("isUpdated", true);
        return "admin/user/edit";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "admin/user/delete";
    }

    @PostMapping("/remove")
    public String delete(Model model, User user) {
        userService.delete(user);
        model.addAttribute("isRemoved", true);
        return "admin/user/delete";
    }

    @ModelAttribute("roles")
    private Set<Role> getAllRoles() {
        return roleService.findAll();
    }

    @ModelAttribute("users")
    private List<User> getAllUsers() {
        return userService.findAll(PageRequest.ofSize(100)).stream().toList();
    }
}
