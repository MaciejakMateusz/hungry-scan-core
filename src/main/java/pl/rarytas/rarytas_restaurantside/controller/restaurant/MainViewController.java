package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class MainViewController {

    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;
    private final RestaurantTableRepository restaurantTableRepository;

    public MainViewController(MenuItemRepository menuItemRepository,
                              OrderService orderService, RestaurantTableRepository restaurantTableRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderService = orderService;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @GetMapping
    public String mainView(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "restaurant/main-view";
    }

    @GetMapping("/menu")
    public String menu() {
        return "/restaurant/menu";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }

    @PostMapping
    public String update(@RequestParam Integer id,
                         @RequestParam boolean paid) {
        orderService.finishOrder(id, paid);
        return "restaurant/main-view";
    }

    @ModelAttribute("menuItems")
    private List<MenuItem> getMenuItems() {
        return menuItemRepository.findAll();
    }

    @ModelAttribute("orders")
    private List<Order> getOrders() {
        return orderService.findAllNotPaid();
    }

    @ModelAttribute("restaurantTables")
    private List<RestaurantTable> getRestaurantTables() {
        return restaurantTableRepository.findAll();
    }
}