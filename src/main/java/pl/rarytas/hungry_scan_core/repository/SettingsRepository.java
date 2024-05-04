package pl.rarytas.hungry_scan_core.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.Settings;

@Repository
public interface SettingsRepository extends CustomRepository<Settings, Integer> {
}