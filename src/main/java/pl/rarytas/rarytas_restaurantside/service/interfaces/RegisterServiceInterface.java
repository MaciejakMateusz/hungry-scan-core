package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.User;

public interface RegisterServiceInterface {
    boolean validate(User user);

    boolean validateAdmin(User user);
}
