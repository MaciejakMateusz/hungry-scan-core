package pl.rarytas.rarytas_restaurantside.testSupport;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.User;

@Component
public class UserBuilder {

    public static User createUser() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setUsername("exampleUser");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }
}