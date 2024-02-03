package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.User;

public interface UserService {

    User findByUsername(String email);

    void update(User user);

    Page<User> findAll(Pageable pageable);

    User findById(Integer id);

    void save(User user);

    void delete(User user);

    boolean existsByEmail(String email);
}
