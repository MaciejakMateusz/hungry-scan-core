package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Statistics;
import org.springframework.stereotype.Service;

@Service
public interface StatisticsService {

    Statistics getStatistics();
}
