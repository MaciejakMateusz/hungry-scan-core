package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.RoleRepository;
import pl.rarytas.rarytas_restaurantside.repository.UserRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RegisterServiceInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Service
@Slf4j
public class RegisterService implements RegisterServiceInterface {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public RegisterService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void saveUser(User user) {
        user.setEnabled(1);
        user.setRoles(new HashSet<>(Collections.singletonList(roleRepository.findByName("ROLE_USER"))));
        userRepository.save(user);
    }

    @Override
    public void saveAdmin(User admin) {
        admin.setEnabled(1);
        admin.setRoles(new HashSet<>(Arrays.asList(
                roleRepository.findByName("ROLE_USER"),
                roleRepository.findByName("ROLE_ADMIN"))));
        userRepository.save(admin);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

