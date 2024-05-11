package pl.rarytas.hungry_scan_core.service.interfaces;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.Statistics;

@Service
public interface StatisticsService {

    Statistics getStatistics();
}
