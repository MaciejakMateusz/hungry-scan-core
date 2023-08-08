package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.UserRepository;

@Service
@Slf4j
public class LoginService {
    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isAuthenticated(User user) {

        boolean isValidated = false;

        if (userRepository.existsByEmail(user.getEmail())) {
            User existingUser = userRepository.findUserByEmail(user.getEmail());
            user.setId(existingUser.getId());
            isValidated = BCrypt.checkpw(user.getPassword(), existingUser.getPassword());
            user.setAdmin(existingUser.isAdmin());
        }
        return isValidated;
    }
}
