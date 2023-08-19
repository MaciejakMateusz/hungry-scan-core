package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.User;

public interface UserServiceInterface {

    User findByUsername(String email);

    void saveUser(User user);

    boolean existsByEmail(String email);
}
