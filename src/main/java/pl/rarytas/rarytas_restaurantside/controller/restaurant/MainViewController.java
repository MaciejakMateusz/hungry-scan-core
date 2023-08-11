package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class MainViewController {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public MainViewController(MenuItemRepository menuItemRepository,
                              OrderRepository orderRepository,
                              RestaurantTableRepository restaurantTableRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
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

    @ModelAttribute("menuItems")
    private List<MenuItem> getMenuItems() {
        return menuItemRepository.findAll();
    }

    @ModelAttribute("orders")
    private List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @ModelAttribute("restaurantTables")
    private List<RestaurantTable> getRestaurantTables() {
        return restaurantTableRepository.findAll();
    }
}