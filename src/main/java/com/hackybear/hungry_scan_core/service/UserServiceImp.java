package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.dto.mapper.MenuMapper;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.RoleRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.utility.RandomPasswordGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ResponseHelper responseHelper;
    private final UserMapper userMapper;
    private final RestaurantMapper restaurantMapper;
    private final MenuMapper menuMapper;
    private final ExceptionHelper exceptionHelper;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final MenuRepository menuRepository;
    private final CacheManager cacheManager;

    @Value("${CMS_APP_URL}")
    private String cmsAppUrl;

    @Override
    public User findByUsername(String username) throws LocalizedException {
        return getUser(username);
    }

    @Override
    @Cacheable(value = USERS_ALL, key = "#username")
    public TreeSet<UserDTO> findAll(String username) throws LocalizedException {
        User currentUser = findByUsername(username);
        Set<User> users = userRepository.findAllByOrganizationId(currentUser.getOrganizationId(), currentUser.getId());
        return users.stream().map(userMapper::toDTO).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public TreeSet<UserActivityDTO> findAllActivity(String username) throws LocalizedException {
        User currentUser = findByUsername(username);
        Set<User> users = userRepository.findAllByOrganizationId(currentUser.getOrganizationId(), currentUser.getId());
        return users.stream().map(userMapper::toUserActivityDTO).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public List<UserDTO> filterUsers(String value, User user) {
        String filterValue = "%" + value.toLowerCase() + "%";
        return userRepository.filterUsers(filterValue, user.getOrganizationId(), user.getUsername())
                .stream()
                .map(userMapper::toDTO).toList();
    }

    @Override
    @Cacheable(value = USER_ID, key = "#user.id")
    public UserProfileDTO getUserProfileData(User user) {
        return userMapper.toUserProfileDTO(user);
    }

    @Override
    @CacheEvict(value = USER_ID, key = "#user.id")
    @Transactional
    public ResponseEntity<?> updateUserProfile(User user, UserProfileUpdateDTO dto, BindingResult br) {
        ResponseEntity<?> errorResponse = validatePasswords(dto, user);
        if (errorResponse != null) return errorResponse;

        if (br.hasErrors()) {
            return responseHelper.createErrorResponse(br);
        }

        if (Objects.nonNull(dto.newPassword())) user.setPassword(BCrypt.hashpw(dto.newPassword(), BCrypt.gensalt()));

        userMapper.updateFromProfileUpdateDTO(dto, user);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override
    public ResponseEntity<?> save(RegistrationDTO registrationDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        Map<String, Object> errorParams = responseHelper.getErrorParams(registrationDTO);
        if (!errorParams.isEmpty()) {
            return ResponseEntity.badRequest().body(errorParams);
        }
        try {
            User user = userMapper.toUser(registrationDTO);
            setUserRoleAsAdmin(user);
            Optional<Long> maxOrganizationId = userRepository.findMaxOrganizationId();
            user.setOrganizationId(maxOrganizationId.orElse(0L) + 1);
            user.setEmail(user.getUsername());
            prepareAndSendActivation(user);
        } catch (MessagingException e) {
            errorParams.put("error", exceptionHelper.getLocalizedMsg("error.register.activationFailed"));
            return ResponseEntity.badRequest().body(errorParams);
        }
        return ResponseEntity.ok(Map.of("redirectUrl", "/activation/?target=" + registrationDTO.username()));
    }

    @Transactional
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    @Override
    public User saveTempUser(User tempUser) {
        tempUser.setEnabled(1);
        return userRepository.save(tempUser);
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
    @Transactional
    public void noteActivity(String username) throws LocalizedException {
        User user = findByUsername(username);
        user.setLastSeenAt(LocalDateTime.now());
        userRepository.save(user);
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
    @CacheEvict(value = USERS_ALL, key = "#callerUsername")
    public ResponseEntity<?> addToOrganization(UserDTO userDTO, BindingResult br, String callerUsername) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        try {
            User currentUser = findByUsername(callerUsername);
            User user = userMapper.toUser(userDTO);
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (!currentUser.getRoles().contains(adminRole) && user.getRoles().contains(adminRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message",
                                exceptionHelper.getLocalizedMsg("validation.user.accessToActionDenied")));
            } else if (userRepository.existsByUsername(userDTO.username())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("username",
                                exceptionHelper.getLocalizedMsg("validation.username.usernameExists")));
            }
            user.setOrganizationId(currentUser.getOrganizationId());
            String tempPassword = RandomPasswordGenerator.generatePassword();
            user.setPassword(tempPassword);
            user.setEnabled(1);
            Restaurant firstRestaurant = user.getRestaurants().iterator().next();
            Long menuId = userRepository.findFirstMenuIdByRestaurantId(firstRestaurant.getId())
                    .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                            "error.restaurantService.restaurantNotFound"));
            user.setActiveRestaurantId(firstRestaurant.getId());
            user.setActiveMenuId(menuId);
            userRepository.save(user);
            emailService.accountCreated(user.getUsername(), tempPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @Override
    @CacheEvict(value = USERS_ALL, key = "#callerUsername")
    public ResponseEntity<?> update(UserDTO userDTO, String callerUsername) {
        try {
            User currentUser = findByUsername(callerUsername);
            User user = userMapper.toUser(userDTO);
            User existing = findByUsername(userDTO.username());
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (!currentUser.getRoles().contains(adminRole) && user.getRoles().contains(adminRole) ||
                    existing.getRoles().contains(adminRole) && !currentUser.getRoles().contains(adminRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message",
                                exceptionHelper.getLocalizedMsg("validation.user.accessToActionDenied")));
            }
            userMapper.updateFromDTO(userDTO, existing);
            setActiveIds(existing);
            userRepository.save(existing);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @Override
    @CacheEvict(value = USER_RESTAURANT, key = "#currentUser.getActiveRestaurantId()")
    public void switchRestaurant(Long restaurantId, User currentUser) throws LocalizedException {
        Long menuId = userRepository.findFirstMenuIdByRestaurantId(restaurantId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound"));
        currentUser.setActiveMenuId(menuId);
        currentUser.setActiveRestaurantId(restaurantId);
        userRepository.save(currentUser);
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
    @Transactional
    @CacheEvict(value = USERS_ALL, key = "#callerUsername")
    public ResponseEntity<?> delete(String username, String callerUsername) {
        try {
            Map<String, Object> params = new HashMap<>();
            User user = findByUsername(username);
            User currentUser = findByUsername(callerUsername);
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (callerUsername.equals(username)) {
                params.put("illegalRemoval", true);
                return ResponseEntity.badRequest().body(params);
            } else if (!currentUser.getRoles().contains(adminRole) && user.getRoles().contains(adminRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message",
                                exceptionHelper.getLocalizedMsg("validation.user.accessToActionDenied")));
            }
            userRepository.deleteByUsername(username);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
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

    @Override
    public boolean isActive(String username) {
        return userRepository.isUserActiveByUsername(username).orElse(false);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private User getUser(String username) throws LocalizedException {
        return userRepository.findByUsername(username)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.userService.userNotFound"));
    }

    private void setActiveIds(User user) {
        Long restaurantId = user.getRestaurants().iterator().next().getId();
        Set<Menu> menus = menuRepository.findAllByRestaurantId(restaurantId);
        Long menuId = menus.iterator().next().getId();
        user.setActiveRestaurantId(restaurantId);
        user.setActiveMenuId(menuId);
        Objects.requireNonNull(cacheManager.getCache(RESTAURANTS_ALL)).evict(user.getId());
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

    private ResponseEntity<?> validatePasswords(UserProfileUpdateDTO dto, User user) {
        if (Objects.isNull(dto.password())) {
            return null;
        }
        if (!isAuthenticated(dto, user)) {
            return createResponse(HttpStatus.UNAUTHORIZED,
                    "password", "validation.wrongPassword");
        } else if (Objects.isNull(dto.newPassword())) {
            return createResponse(HttpStatus.BAD_REQUEST,
                    "newPassword", "validation.newPassword.notBlank");
        } else if (!dto.newPassword().equals(dto.repeatedPassword())) {
            return createResponse(HttpStatus.BAD_REQUEST,
                    "repeatedPassword", "validation.repeatedPassword.notMatch");
        }
        return null;
    }

    private ResponseEntity<?> createResponse(HttpStatus status, String key, String messageCode) {
        return ResponseEntity.status(status).body(Map.of(key, exceptionHelper.getLocalizedMsg(messageCode)));
    }

    private boolean isAuthenticated(UserProfileUpdateDTO dto, User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), dto.password());
        boolean isAuthenticated;
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            isAuthenticated = authentication.isAuthenticated();
        } catch (Exception e) {
            isAuthenticated = false;
        }
        return isAuthenticated;
    }

}