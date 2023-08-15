package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.UserRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RegisterServiceInterface;

@Service
@Slf4j
public class RegisterService implements RegisterServiceInterface {

    private final UserRepository userRepository;

    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean validate(User user) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            userRepository.save(user);
            return true;
        }
        return false;
    }
}

