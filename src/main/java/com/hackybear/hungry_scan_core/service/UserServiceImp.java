package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RoleRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ExceptionHelper exceptionHelper;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, ExceptionHelper exceptionHelper, RoleRepository roleRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.exceptionHelper = exceptionHelper;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @Override
    public User findByUsername(String username) throws LocalizedException {
        return getUser(username);
    }

    @Override
    public List<User> findAll() throws LocalizedException {
        User currentUser = getCurrentUser();
        return userRepository.findAllByOrganizationId(currentUser.getOrganizationId());
    }

    @Override
    public void save(RegistrationDTO registrationDTO) {
        User user = userMapper.toUser(registrationDTO);
        setUserRoleAsAdmin(user);
        Optional<Long> maxOrganizationId = userRepository.findMaxOrganizationId();
        user.setOrganizationId(maxOrganizationId.orElse(0L) + 1);
        user.setEmail(user.getUsername());
        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        userRepository.save(user);
        emailService.activateAccount(user.getEmail(), emailToken);
    }

    @Override
    public void saveTempUser(User tempUser) {
        tempUser.setEnabled(1);
        userRepository.save(tempUser);
    }

    @Override
    public void activateAccount(String emailToken) throws LocalizedException {
        User user = userRepository.findByEmailToken(emailToken)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.userService.userNotFound"));
        user.setEnabled(1);
        user.setEmailToken(null);
        userRepository.save(user);
    }

    @Override
    public void sendPasswordRecovery(String email) {
        Optional<User> optionalUser = userRepository.findByUsername(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String emailToken = UUID.randomUUID().toString();
            user.setEmailToken(emailToken);
            userRepository.save(user);
            emailService.passwordRecovery(email, emailToken);
        }
    }

    @Override
    public void recoverPassword(RecoveryDTO recoveryDTO) throws LocalizedException {
        if (!recoveryDTO.password().equals(recoveryDTO.repeatedPassword())) {
            throw new LocalizedException("Passwords not match");
        }
        Optional<User> optionalUser = userRepository.findByEmailToken(recoveryDTO.emailToken());
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        if (user.getEmailTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new LocalizedException("Email token expired");
        }
        user.setPassword(recoveryDTO.password());
        user.setEmailToken(null);
        user.setEmailTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public void addToOrganization(RegistrationDTO registrationDTO) throws LocalizedException {
        User currentUser = getCurrentUser();
        User user = userMapper.toUser(registrationDTO);
        user.setOrganizationId(currentUser.getOrganizationId());
        user.setEmail(user.getUsername());
        user.setEnabled(1);
        userRepository.save(user);
    }

    @Override
    public void update(RegistrationDTO registrationDTO) throws LocalizedException {
        User user = findByUsername(registrationDTO.username());
        user.setUsername(registrationDTO.username());
        user.setName(registrationDTO.name());
        user.setSurname(registrationDTO.surname());
        user.setEmail(registrationDTO.email());
        userRepository.save(user);
    }

    @Override
    public void switchRestaurant(Long restaurantId) throws LocalizedException {
        User user = getCurrentUser();
        user.setActiveRestaurantId(restaurantId);
        userRepository.save(user);
    }

    @Override
    public void switchMenu(Long menuId) throws LocalizedException {
        User user = getCurrentUser();
        user.setActiveMenuId(menuId);
        userRepository.save(user);
    }

    @Override
    public void delete(String username) throws LocalizedException {
        userRepository.deleteByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isUpdatedUserValid(RegistrationDTO registrationDTO) throws LocalizedException {
        return "".equals(getErrorParam(registrationDTO));
    }

    @Override
    public String getErrorParam(RegistrationDTO registrationDTO) throws LocalizedException {
        User modifiedUser = getUser(registrationDTO.username());

        if (existsByUsername(registrationDTO.username()) && !registrationDTO.username().equals(modifiedUser.getUsername())) {
            return "userNameExists";
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
    public User getCurrentUser() throws LocalizedException {
        return getUser(getAuthentication().getName());
    }

    @Override
    public Long getActiveRestaurantId() {
        return userRepository.getActiveRestaurantIdByUsername(getAuthentication().getName());
    }

    @Override
    public Long getActiveMenuId() {
        return userRepository.getActiveMenuIdByUsername(getAuthentication().getName());
    }

    @Override
    public int isEnabled(String username) {
        return userRepository.isUserEnabledByUsername(username).orElse(0);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private User getUser(String username) throws LocalizedException {
        return userRepository.findByUsername(username)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.userService.userNotFound"));
    }

    private void setUserRoleAsAdmin(User user) {
        Role role = roleRepository.findByName("ROLE_ADMIN");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
    }

}