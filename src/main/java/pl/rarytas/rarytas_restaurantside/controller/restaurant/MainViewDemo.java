package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class MainViewDemo {

    @GetMapping
    public String demo() {
        return "index";
    }

}