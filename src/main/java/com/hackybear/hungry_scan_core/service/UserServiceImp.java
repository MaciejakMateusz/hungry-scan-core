package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RoleRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ExceptionHelper exceptionHelper;
    private final RoleRepository roleRepository;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, ExceptionHelper exceptionHelper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.exceptionHelper = exceptionHelper;
        this.roleRepository = roleRepository;
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
        Long maxOrganizationId = userRepository.findMaxOrganizationId();
        user.setOrganizationId(Objects.nonNull(maxOrganizationId) ? maxOrganizationId + 1 : 1);
        user.setEmail(user.getUsername());
        userRepository.save(user);
    }

    @Override
    public void addToOrganization(RegistrationDTO registrationDTO) throws LocalizedException {
        User currentUser = getCurrentUser();
        User user = userMapper.toUser(registrationDTO);
        user.setOrganizationId(currentUser.getOrganizationId());
        user.setEmail(user.getUsername());
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