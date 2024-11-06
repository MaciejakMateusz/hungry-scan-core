package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.JwtToken;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CustomerAccessRemover {

    private final UserService userService;

    public CustomerAccessRemover(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 60000 * 60)
    @Transactional
    public void controlJwtAndRemoveUsers() {
        log.info("Running controlJwtAndRemoveUsers() job...");
        List<User> users = userService.findAllCustomers();
        for (User user : users) {
            JwtToken token = getAccessToken(user);
            if (Objects.nonNull(token) && isTokenExpired(token)) {
                handleDeletion(user);
            }
        }
        log.info("Finished controlJwtAndRemoveUsers() job.");
    }

    private JwtToken getAccessToken(User user) {
        JwtToken token = user.getJwtToken();
        return Objects.nonNull(token) ? token : null;
    }

    private boolean isTokenExpired(JwtToken token) {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(3);
        return token.getCreated().isBefore(expirationTime);
    }

    private void handleDeletion(User user) {
        try {
            userService.delete(user.getUsername());
            log.info("CustomerAccessRemover - User {} deleted", user.getId());
        } catch (LocalizedException e) {
            log.error("CustomerAccessRemover - User {} could not be deleted", user.getId(), e);
        }
    }
}