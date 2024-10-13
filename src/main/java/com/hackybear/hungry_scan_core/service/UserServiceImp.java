package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        User currentUser = getCurrentUser();
        return userRepository.findAllByOrganizationId(currentUser.getOrganizationId());
    }

    @Override
    public User findById(Long id) throws LocalizedException {
        return userRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.userService.userNotFound", id));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
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

    @Override
    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is null");
        }
        return userRepository.findUserByUsername(authentication.getName());
    }
}