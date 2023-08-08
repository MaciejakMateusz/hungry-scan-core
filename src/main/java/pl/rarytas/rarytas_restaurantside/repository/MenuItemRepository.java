package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

}
