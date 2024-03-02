package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/restaurant/cms/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    @GetMapping
    public String restaurantsList() {
        return "restaurant/cms/restaurants/list";
    }

    @GetMapping("/add")
    public String addRestaurant(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurant/cms/restaurants/add";
    }

    @PostMapping("/add")
    public String addRestaurant(@Valid Restaurant restaurant,
                                BindingResult br) {
        if (br.hasErrors()) {
            return "restaurant/cms/restaurants/add";
        }
        restaurantService.save(restaurant);
        return "redirect:/restaurant/cms/restaurants";
    }

    @PostMapping("/edit")
    public String updateRestaurant(Model model,
                                   @RequestParam Integer id) {
        model.addAttribute("restaurant", restaurantService.findById(id).orElseThrow());
        return "restaurant/cms/restaurants/edit";
    }

    @PostMapping("/update")
    public String updateRestaurant(@Valid Restaurant restaurant,
                                   BindingResult br) {
        if (br.hasErrors()) {
            return "restaurant/cms/restaurants/edit";
        }
        restaurantService.save(restaurant);
        return "redirect:/restaurant/cms/restaurants";
    }

    @PostMapping("/delete")
    public String deleteItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("restaurant", restaurantService.findById(id).orElseThrow());
        return "restaurant/cms/restaurants/delete";
    }

    @PostMapping("/remove")
    public String deleteItem(@ModelAttribute Restaurant restaurant) {
        restaurantService.delete(restaurant);
        return "redirect:/restaurant/cms/restaurants";
    }

    @ModelAttribute("restaurants")
    private List<Restaurant> getAllItems() {
        return restaurantService.findAll();
    }
}
