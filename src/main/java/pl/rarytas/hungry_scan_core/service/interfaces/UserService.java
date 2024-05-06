package pl.rarytas.hungry_scan_core.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.hungry_scan_core.entity.User;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface UserService {

    User findByUsername(String email);

    void update(User user);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    User findById(Integer id) throws LocalizedException;

    void save(User user);

    void delete(Integer id) throws LocalizedException;

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean isUpdatedUserValid(User user) throws LocalizedException;

    String getErrorParam(User user) throws LocalizedException;

    List<User> findAllByRole(String roleName);

    List<User> findAllCustomers();
}