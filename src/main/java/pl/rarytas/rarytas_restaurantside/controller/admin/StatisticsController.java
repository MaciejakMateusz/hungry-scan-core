package pl.rarytas.rarytas_restaurantside.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.rarytas.rarytas_restaurantside.entity.Statistics;
import pl.rarytas.rarytas_restaurantside.service.interfaces.StatisticsService;

@RestController
@Slf4j
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/show")
    public ResponseEntity<Statistics> show() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}