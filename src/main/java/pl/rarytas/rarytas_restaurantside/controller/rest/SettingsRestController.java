package pl.rarytas.rarytas_restaurantside.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;

/**
 * GET
 * /api/settings - returns settings for a restaurant with ID specified in environment variables
 * PATCH
 * /api/settings - updates settings
 **/

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class SettingsRestController {

    private final SettingsService settingsService;

    public SettingsRestController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public Settings getSettings() {
        return settingsService.getSettings();
    }

    @PatchMapping
    public void updateSettings(@RequestBody @Valid Settings settings, BindingResult br) throws LocalizedException {
        if (br.hasErrors()) {
            throw new LocalizedException(br.getAllErrors().toString());
        }
        settingsService.save(settings);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
