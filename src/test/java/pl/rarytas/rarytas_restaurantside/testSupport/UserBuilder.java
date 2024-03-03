package pl.rarytas.rarytas_restaurantside.testSupport;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.User;

@Component
public class UserBuilder {

    public static User createCorrectUser() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setUsername("exampleUser");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    public static User createCorrectAdmin() {
        User user = new User();
        user.setEmail("admin@admino.com");
        user.setUsername("exampleAdmin");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    public static User createIncorrectUser() {
        User user = new User();
        user.setEmail("example@example");
        user.setUsername("ex");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Exhale1!");
        return user;
    }
}