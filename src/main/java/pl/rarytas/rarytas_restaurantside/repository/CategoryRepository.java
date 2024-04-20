package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT Category from Category c WHERE c.isAvailable = true ORDER BY c.displayOrder")
    List<Category> findAllAvailable();

    @Query("SELECT c FROM Category c WHERE :menuItem IN elements(c.menuItems)")
    Optional<Category> findByMenuItem(@Param("menuItem") MenuItem menuItem);

}
