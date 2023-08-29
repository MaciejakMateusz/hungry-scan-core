package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class MainViewController {
    private final CategoryService categoryService;

    public MainViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String mainView() {
        return "restaurant/helicopter-view";
    }

    @GetMapping("/menu")
    public String menu() {
        return "restaurant/menu/menu";
    }


    @ModelAttribute("categories")
    private List<Category> getEntireMenu() {
        return categoryService.findAll();
    }

}