package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Set;

@Controller
@RequestMapping("/restaurant/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String bookings(Model model) {
        model.addAttribute("booking", new Booking());
        return "restaurant/bookings/bookings";
    }

    @PostMapping
    public String save(@Valid Booking booking, BindingResult br, Model model) throws LocalizedException {
        if (br.hasErrors()) {
            return "restaurant/bookings/bookings";
        }
        bookingService.save(booking);
        model.addAttribute("isBooked", true);
        return "restaurant/bookings/bookings";
    }

    @ModelAttribute("weeklyBookings")
    private Set<Booking> getWeeklyBookings() {
        int year = LocalDate.now().getYear();
        int week = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return bookingService.findAllByWeek(year, week);
    }

}