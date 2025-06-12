package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MenuPlanUpdater {

    public void updateMenusPlans(Restaurant restaurant, RestaurantDTO restaurantDTO) {
        Settings settings = restaurant.getSettings();
        SettingsDTO settingsDTO = restaurantDTO.settings();
        Map<DayOfWeek, TimeRange> oldOperatingHours = settings.getOperatingHours();
        Map<DayOfWeek, TimeRange> newOperatingHours = settingsDTO.operatingHours();

        if (oldOperatingHours.equals(newOperatingHours)) {
            return;
        }

        for (Menu menu : restaurant.getMenus()) {
            for (MenuPlan plan : new HashSet<>(menu.getPlan())) {
                DayOfWeek day = plan.getDayOfWeek();
                TimeRange oldOp = oldOperatingHours.get(day);
                TimeRange newOp = newOperatingHours.get(day);

                if (newOp == null || oldOp == null) {
                    menu.getPlan().remove(plan);
                    continue;
                }

                Set<TimeRange> trimmed = new HashSet<>();
                for (TimeRange tr : plan.getTimeRanges()) {
                    TimeRange overlap = tr.intersect(newOp);
                    if (overlap != null) {
                        overlap.withAvailable(tr.isAvailable());
                        trimmed.add(overlap);
                    }
                }

                if (trimmed.isEmpty()) {
                    menu.getPlan().remove(plan);
                    continue;
                }

                final int MINUTES_PER_DAY = 24 * 60;
                int oldStart = oldOp.getStartTime().getHour() * 60 + oldOp.getStartTime().getMinute();
                int oldEndRaw = oldOp.getEndTime().getHour() * 60 + oldOp.getEndTime().getMinute();
                int newStart = newOp.getStartTime().getHour() * 60 + newOp.getStartTime().getMinute();
                int newEndRaw = newOp.getEndTime().getHour() * 60 + newOp.getEndTime().getMinute();

                int oldNormEnd = oldEndRaw > oldStart ? oldEndRaw : oldEndRaw + MINUTES_PER_DAY;
                int newNormEnd = newEndRaw > newStart ? newEndRaw : newEndRaw + MINUTES_PER_DAY;

                boolean extendHead = newStart < oldStart;
                boolean extendTail = newNormEnd > oldNormEnd;

                if (extendHead) {
                    TimeRange best = null;
                    int bestDist = Integer.MAX_VALUE;
                    for (TimeRange tr : trimmed) {
                        int trStart = tr.getStartTime().getHour() * 60 + tr.getStartTime().getMinute();
                        int normStart = trStart >= newStart ? trStart : trStart + MINUTES_PER_DAY;
                        int dist = normStart - newStart;
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = tr;
                        }
                    }
                    if (best != null) {
                        best.setStartTime(newOp.getStartTime());
                    }
                }

                if (extendTail) {
                    TimeRange best = null;
                    int bestDist = Integer.MAX_VALUE;
                    for (TimeRange tr : trimmed) {
                        int trEnd = tr.getEndTime().getHour() * 60 + tr.getEndTime().getMinute();
                        int normEnd = trEnd >= newStart ? trEnd : trEnd + MINUTES_PER_DAY;
                        int dist = newNormEnd - normEnd;
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = tr;
                        }
                    }
                    if (best != null) {
                        best.setEndTime(newOp.getEndTime());
                    }
                }

                plan.getTimeRanges().clear();
                plan.getTimeRanges().addAll(trimmed);
            }
        }
    }
}
