package pl.rarytas.hungry_scan_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.MenuItemVariant;

@Repository
public interface MenuItemVariantRepository extends JpaRepository<MenuItemVariant, Integer> {

}