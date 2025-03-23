package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.MenuDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RoleRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;

import static com.hackybear.hungry_scan_core.utility.Fields.USER_MENU_ID;
import static com.hackybear.hungry_scan_core.utility.Fields.USER_RESTAURANT_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final ResponseHelper responseHelper;
    private final UserMapper userMapper;
    private final RestaurantMapper restaurantMapper;
    private final MenuMapper menuMapper;
    private final ExceptionHelper exceptionHelper;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Value("${CMS_APP_URL}")
    private String cmsAppUrl;

    @Override
    public User findByUsername(String username) throws LocalizedException {
        return getUser(username);
    }

    @Override
    public TreeSet<User> findAll() throws LocalizedException {
        User currentUser = getCurrentUser();
        return new TreeSet<>(userRepository.findAllByOrganizationId(currentUser.getOrganizationId(), currentUser.getId()));
    }

    @Transactional
    @Override
    public void save(RegistrationDTO registrationDTO) throws MessagingException {
        User user = userMapper.toUser(registrationDTO);
        setUserRoleAsAdmin(user);
        Optional<Long> maxOrganizationId = userRepository.findMaxOrganizationId();
        user.setOrganizationId(maxOrganizationId.orElse(0L) + 1);
        user.setEmail(user.getUsername());
        prepareAndSendActivation(user);
    }

    @Transactional
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
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
    public void resendActivation(String email) throws LocalizedException, MessagingException {
        User user = findByUsername(email);
        prepareAndSendActivation(user);
    }

    @Override
    public void sendPasswordRecovery(String email) throws MessagingException {
        Optional<User> optionalUser = userRepository.findByUsername(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String emailToken = UUID.randomUUID().toString();
            user.setEmailToken(emailToken);
            user.setEmailTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            emailService.passwordRecovery(email, emailToken);
        }
    }

    @Override
    public ResponseEntity<?> recoverPassword(RecoveryDTO recovery, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        Map<String, Object> errorParams = new HashMap<>();
        if (!recovery.password().equals(recovery.repeatedPassword())) {
            errorParams.put("repeatedPassword", exceptionHelper.getLocalizedMsg("validation.repeatedPassword.notMatch"));
            return ResponseEntity.badRequest().body(errorParams);
        }
        Optional<User> optionalUser = userRepository.findByEmailToken(recovery.emailToken());
        if (optionalUser.isEmpty()) {
            errorParams.put("error", exceptionHelper.getLocalizedMsg("validation.recovery.invalidToken"));
            return ResponseEntity.badRequest().body(errorParams);
        }
        User user = optionalUser.get();
        if (user.getEmailTokenExpiry().isBefore(LocalDateTime.now())) {
            errorParams.put("error", exceptionHelper.getLocalizedMsg("validation.recovery.tokenExpired"));
            return ResponseEntity.badRequest().body(errorParams);
        }
        user.setPassword(BCrypt.hashpw(recovery.password(), BCrypt.gensalt()));
        user.setEmailToken(null);
        user.setEmailTokenExpiry(null);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("redirectUrl", cmsAppUrl + "/recovery-confirmation"));
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
        user.setForename(registrationDTO.forename());
        user.setSurname(registrationDTO.surname());
        user.setEmail(registrationDTO.email());
        userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = USER_RESTAURANT_ID, key = "#user.getUsername()")
    })
    public void switchRestaurant(Long restaurantId, User user) {
        user.setActiveRestaurantId(restaurantId);
        userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = USER_MENU_ID, key = "#user.getUsername()")
    })
    public void switchMenu(Long menuId, User user) {
        user.setActiveMenuId(menuId);
        userRepository.save(user);
    }

    @Override
    public void delete(String username) throws LocalizedException {
        userRepository.deleteByUsername(username);
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
    public Long getActiveRestaurantId() throws LocalizedException {
        return userRepository.getActiveRestaurantIdByUsername(getAuthentication().getName())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
    }

    @Override
    public RestaurantDTO getCurrentRestaurant() throws LocalizedException {
        Optional<Restaurant> result = userRepository.getCurrentRestaurantByUsername(getAuthentication().getName());
        Restaurant restaurant = result.orElseThrow(exceptionHelper.supplyLocalizedMessage(
                "error.restaurantService.restaurantNotFound"));
        return restaurantMapper.toDTO(restaurant);
    }

    @Override
    public boolean hasCreatedRestaurant() {
        return userRepository.getActiveRestaurantIdByUsername(getAuthentication().getName()).isPresent();
    }

    @Override
    public boolean hasCreatedRestaurant(String username) {
        return userRepository.getActiveRestaurantIdByUsername(username).isPresent();
    }

    @Override
    public Long getActiveMenuId() throws LocalizedException {
        return userRepository.getActiveMenuIdByUsername(getAuthentication().getName())
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuService.menuNotFound"));
    }

    @Override
    public MenuDTO getCurrentMenu() throws LocalizedException {
        Optional<Menu> result = userRepository.getCurrentMenuByUsername(getAuthentication().getName());
        Menu menu = result.orElseThrow(exceptionHelper.supplyLocalizedMessage(
                "error.menuService.menuNotFound"));
        return menuMapper.toDTO(menu);
    }

    @Override
    public int isEnabled(String username) {
        return userRepository.isUserEnabledByUsername(username).orElse(0);
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
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

    private void prepareAndSendActivation(User user) throws MessagingException {
        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        userRepository.save(user);
        emailService.activateAccount(user.getEmail(), emailToken);
    }

}