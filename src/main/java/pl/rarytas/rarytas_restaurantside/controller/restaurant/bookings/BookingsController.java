package pl.rarytas.rarytas_restaurantside.controller.restaurant.bookings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurant/bookings")
public class BookingsController {

    @GetMapping
    public String bookings() {
        return "restaurant/bookings/bookings";
    }

}