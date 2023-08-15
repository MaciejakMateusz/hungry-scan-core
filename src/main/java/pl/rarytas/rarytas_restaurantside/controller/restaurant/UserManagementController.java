package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.LoginService;
import pl.rarytas.rarytas_restaurantside.service.RegisterService;


@Controller
@RequestMapping("/")
public class UserManagementController {

    private final LoginService loginService;
    private final RegisterService registerService;

    public UserManagementController(
            LoginService loginService,
            RegisterService registerService) {
        this.loginService = loginService;
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
        if (registerService.validate(user)) {
            return "success-registration";
        } else {
            model.addAttribute("userExists", true);
            return "register";
        }
    }

    @PostMapping("/registerAdmin")
    public String registerAdmin(@Valid User user, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "registerAdmin";
        }
        if (!user.getPassword().equals(user.getRepeatedPassword())) {
            model.addAttribute("passwordsNotMatch", true);
            return "registerAdmin";
        }
        if (registerService.validate(user)) {
            return "success-registration";
        } else {
            model.addAttribute("userExists", true);
            return "registerAdmin";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(User user, Model model, HttpSession session) {
        if (loginService.isAuthenticated(user)) {
            session.setAttribute("user", user);
            return "redirect:/restaurant";
        }
        model.addAttribute("isAuthenticated", false);
        return "login";
    }

}
