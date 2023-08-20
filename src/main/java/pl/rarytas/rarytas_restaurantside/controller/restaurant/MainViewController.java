package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class MainViewController {
    private final OrderService orderService;
    private final CategoryService categoryService;

    public MainViewController(OrderService orderService,
                              CategoryService categoryService) {
        this.orderService = orderService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String mainView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("user", userDetails);
        return "restaurant/main-view";
    }

    @GetMapping("/menu")
    public String menu() {
        return "restaurant/menu/menu";
    }


    @PostMapping
    public String finalizeMainViewOrder(@RequestParam Integer id,
                                        @RequestParam boolean paid,
                                        @RequestParam boolean isResolved) {
        orderService.finish(id, paid, isResolved);
        return "restaurant/main-view";
    }


    @ModelAttribute("categories")
    private List<Category> getEntireMenu() {
        return categoryService.findAll();
    }

}