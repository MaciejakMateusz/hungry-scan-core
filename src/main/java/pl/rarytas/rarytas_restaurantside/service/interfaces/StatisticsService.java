package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Statistics;

@Service
public interface StatisticsService {

    Statistics getStatistics();
}
