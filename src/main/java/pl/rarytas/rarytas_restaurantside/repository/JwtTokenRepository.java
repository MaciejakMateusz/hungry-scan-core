package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {
}
