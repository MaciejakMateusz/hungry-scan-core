package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurant")
public class MainViewController {

    @GetMapping
    public String mainView() {
        return "restaurant/main-view";
    }

    @GetMapping("/menu")
    public String menu() {
        return "restaurant/menu/menu";
    }

}