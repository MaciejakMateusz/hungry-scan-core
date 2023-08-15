package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.User;

public interface LoginServiceInterface {
    boolean isAuthenticated(User user);
}
