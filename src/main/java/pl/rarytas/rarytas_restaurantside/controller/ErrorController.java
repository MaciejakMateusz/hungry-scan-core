package pl.rarytas.rarytas_restaurantside.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping
    public String error(Model model, ErrorResponse errorResponse) {
        model.addAttribute("error", errorResponse);
        return "error";
    }
}
