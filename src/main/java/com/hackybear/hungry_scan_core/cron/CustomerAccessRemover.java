package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.JwtToken;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerAccessRemover {

    private final UserService userService;

    @Transactional
    public void controlJwtAndRemoveUsers() {
        List<User> users = userService.findAllCustomers();
        for (User user : users) {
            JwtToken token = getAccessToken(user);
            if (Objects.nonNull(token) && isTokenExpired(token)) {
                handleDeletion(user);
            }
        }
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
        } catch (LocalizedException e) {
            log.error("CustomerAccessRemover - User {} could not be deleted", user.getId(), e);
        }
    }
}