package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Statistics;
import com.hackybear.hungry_scan_core.repository.StatisticsRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.service.interfaces.StatisticsService;
import org.springframework.stereotype.Service;

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
