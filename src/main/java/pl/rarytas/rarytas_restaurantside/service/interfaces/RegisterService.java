package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.User;

public interface RegisterService {
    void saveUser(User user);

    void saveAdmin(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
