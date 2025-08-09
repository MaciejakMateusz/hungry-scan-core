package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;

public interface UserService {

    User findByUsername(String email) throws LocalizedException;

    Set<User> findAll() throws LocalizedException;

    void save(RegistrationDTO registrationDTO) throws MessagingException;

    void saveTempUser(User tempUser);

    void save(User user);

    void activateAccount(String emailToken) throws LocalizedException;

    void resendActivation(String email) throws LocalizedException, MessagingException;

    void sendPasswordRecovery(String email) throws LocalizedException, MessagingException;

    ResponseEntity<?> recoverPassword(RecoveryDTO recovery, BindingResult br);

    ResponseEntity<?> addToOrganization(RegistrationDTO registrationDTO, BindingResult br);

    void update(RegistrationDTO registrationDTO) throws LocalizedException;

    void delete(String username) throws LocalizedException;

    void switchRestaurant(Long restaurantId, User user) throws LocalizedException;

    void switchMenu(Long menuId, User user);

    boolean isUpdatedUserValid(RegistrationDTO registrationDTO) throws LocalizedException;

    String getErrorParam(RegistrationDTO registrationDTO) throws LocalizedException;

    List<User> findAllByRole(String roleName);

    List<User> findAllCustomers();

    User getCurrentUser() throws LocalizedException;

    Long getActiveRestaurantId() throws LocalizedException;

    RestaurantDTO getCurrentRestaurant() throws LocalizedException;

    boolean hasCreatedRestaurant();

    boolean hasCreatedRestaurant(String username);

    Long getActiveMenuId() throws LocalizedException;

    MenuDTO getCurrentMenu() throws LocalizedException;

    int isEnabled(String username) throws LocalizedException;

}
