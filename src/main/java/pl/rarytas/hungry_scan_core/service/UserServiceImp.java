package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.User;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.UserRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.UserService;

import java.util.List;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final ExceptionHelper exceptionHelper;

    public UserServiceImp(UserRepository userRepository, ExceptionHelper exceptionHelper) {
        this.userRepository = userRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Integer id) throws LocalizedException {
        return userRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.userService.userNotFound", id));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        User existingUser = findById(id);
        userRepository.delete(existingUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isUpdatedUserValid(User user) throws LocalizedException {
        return "".equals(getErrorParam(user));
    }

    @Override
    public String getErrorParam(User user) throws LocalizedException {
        User modifiedUser = findById(user.getId());

        if (existsByUsername(user.getUsername()) && !user.getUsername().equals(modifiedUser.getUsername())) {
            return "userNameExists";
        } else if (existsByEmail(user.getEmail()) && !user.getEmail().equals(modifiedUser.getEmail())) {
            return "emailExists";
        }
        return "";
    }

    @Override
    public List<User> findAllByRole(String roleName) {
        return userRepository.findByRole(roleName);
    }

    @Override
    public List<User> findAllCustomers() {
        return userRepository.findAllCustomers();
    }
}