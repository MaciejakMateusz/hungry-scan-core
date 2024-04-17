package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
}
