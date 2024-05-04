package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.entity.Statistics;
import pl.rarytas.hungry_scan_core.repository.StatisticsRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.MenuItemService;
import pl.rarytas.hungry_scan_core.service.interfaces.StatisticsService;

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
