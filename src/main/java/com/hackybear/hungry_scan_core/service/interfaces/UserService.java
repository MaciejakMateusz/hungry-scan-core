package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface UserService {

    User findByUsername(String email) throws LocalizedException;

    Set<UserDTO> findAll(String username) throws LocalizedException;

    TreeSet<UserActivityDTO> findAllActivity(String username) throws LocalizedException;

    List<UserDTO> filterUsers(String value, User user);

    UserProfileDTO getUserProfileData(User user);

    ResponseEntity<?> updateUserProfile(User user, UserProfileUpdateDTO dto, BindingResult br);

    ResponseEntity<?> save(RegistrationDTO registrationDTO, BindingResult br);

    User saveTempUser(User tempUser);

    void save(User user);

    void activateAccount(String emailToken) throws LocalizedException;

    void resendActivation(String email) throws LocalizedException, MessagingException;

    void sendPasswordRecovery(String email) throws LocalizedException, MessagingException;

    void noteActivity(String username) throws LocalizedException;

    ResponseEntity<?> recoverPassword(RecoveryDTO recovery, BindingResult br);

    ResponseEntity<?> addToOrganization(UserDTO userDTO, BindingResult br, String callerUsername);

    ResponseEntity<?> update(UserDTO userDTO, String callerUsername);

    ResponseEntity<?> delete(String username, String callerUsername);

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

    boolean isActive(String username);
}
