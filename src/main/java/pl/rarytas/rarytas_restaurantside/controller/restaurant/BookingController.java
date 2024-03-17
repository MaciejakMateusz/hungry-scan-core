package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ResponseHelper responseHelper;

    public BookingController(BookingService bookingService, ResponseHelper responseHelper) {
        this.bookingService = bookingService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, bookingService::findById);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody @Valid Booking booking, BindingResult br) {
        return responseHelper.buildResponse(booking, br, bookingService::save);
    }

    //TODO add DELETE method and implement service method to remove bookings

    @PostMapping("/date")
    public ResponseEntity<Page<Booking>> getByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, bookingService::findAllByDateBetween);
    }

    @GetMapping("/count-all")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(bookingService.countAll());
    }

    @PostMapping("/count-dates")
    public ResponseEntity<Long> countByDateBetween(@RequestBody LocalDate dateFrom,
                                                   @RequestBody LocalDate dateTo) {
        return ResponseEntity.ok(bookingService.countByDateBetween(dateFrom, dateTo));
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}