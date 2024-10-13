package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.List;

public interface UserService {

    User findByUsername(String email);

    void update(User user);

    List<User> findAll();

    User findById(Long id) throws LocalizedException;

    void save(User user);

    void delete(Long id) throws LocalizedException;

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean isUpdatedUserValid(User user) throws LocalizedException;

    String getErrorParam(User user) throws LocalizedException;

    List<User> findAllByRole(String roleName);

    List<User> findAllCustomers();

    User getCurrentUser() throws AuthenticationException;
}
