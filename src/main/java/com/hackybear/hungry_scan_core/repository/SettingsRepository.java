package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Settings;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CustomRepository<Settings, Integer> {
}