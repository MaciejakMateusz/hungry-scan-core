package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.Statistics;
import pl.rarytas.rarytas_restaurantside.repository.StatisticsRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.StatisticsService;

import java.util.List;

@Service
public class StatisticsServiceImp implements StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final MenuItemService menuItemService;

    public StatisticsServiceImp(StatisticsRepository statisticsRepository, MenuItemService menuItemService) {
        this.statisticsRepository = statisticsRepository;
        this.menuItemService = menuItemService;
    }

    @Override
    public Statistics getStatistics() {
        Statistics statistics = statisticsRepository.findById(1).orElseThrow();
        List<MenuItem> menuItems = menuItemService.findAll();
        statistics.setMenuItems(menuItems);
        return statistics;
    }

}
