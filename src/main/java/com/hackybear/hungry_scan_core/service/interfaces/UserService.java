package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface UserService {

    User findByUsername(String email) throws LocalizedException;

    List<User> findAll() throws LocalizedException;

    void save(RegistrationDTO registrationDTO);

    void saveTempUser(User tempUser);

    void activateAccount(String emailToken) throws LocalizedException;

    void sendPasswordRecovery(String email) throws LocalizedException;

    void recoverPassword(RecoveryDTO recovery) throws LocalizedException;

    void addToOrganization(RegistrationDTO registrationDTO) throws LocalizedException;

    void update(RegistrationDTO registrationDTO) throws LocalizedException;

    void delete(String username) throws LocalizedException;

    void switchRestaurant(Long restaurantId) throws LocalizedException;

    void switchMenu(Long menuId) throws LocalizedException;

    boolean existsByUsername(String username);

    boolean isUpdatedUserValid(RegistrationDTO registrationDTO) throws LocalizedException;

    String getErrorParam(RegistrationDTO registrationDTO) throws LocalizedException;

    List<User> findAllByRole(String roleName);

    List<User> findAllCustomers();

    User getCurrentUser() throws LocalizedException;

    Long getActiveRestaurantId() throws LocalizedException;

    Long getActiveMenuId() throws LocalizedException;

    int isEnabled(String username) throws LocalizedException;
}
