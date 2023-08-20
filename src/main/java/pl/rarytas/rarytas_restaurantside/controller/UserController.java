package pl.rarytas.rarytas_restaurantside.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.RegisterService;


@Controller
public class UserController {

    private final RegisterService registerService;

    public UserController(
            RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/register/{code}")
    public String adminRegister(Model model, @PathVariable Integer code) {
        model.addAttribute("user", new User());
        if (code == 667560608) {
            return "registerAdmin";
        }
        return "redirect:/register";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "register";
        }
        if (!user.getPassword().equals(user.getRepeatedPassword())) {
            model.addAttribute("passwordsNotMatch", true);
            return "register";
        }
        registerService.saveUser(user);
        return "success-registration";
    }

    @PostMapping("/registerAdmin")
    public String registerAdmin(@Valid User admin, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "registerAdmin";
        }
        if (!admin.getPassword().equals(admin.getRepeatedPassword())) {
            model.addAttribute("passwordsNotMatch", true);
            return "registerAdmin";
        }
        registerService.saveAdmin(admin);
        return "success-registration";

    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

}
