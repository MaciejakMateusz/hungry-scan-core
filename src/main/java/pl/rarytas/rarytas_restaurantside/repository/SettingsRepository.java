package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.Settings;

@Repository
public interface SettingsRepository extends CustomRepository<Settings, Integer> {
}