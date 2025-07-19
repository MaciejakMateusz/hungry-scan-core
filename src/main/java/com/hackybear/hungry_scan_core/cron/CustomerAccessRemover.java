package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.JwtToken;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.JwtTokenRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerAccessRemover {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void controlJwtAndRemoveUsers() {
        List<User> users = userService.findAllCustomers();
        List<User> expiredUsers = users.stream()
                .filter(this::isTokenExpired)
                .toList();
        List<Long> expiredJwtTokens = expiredUsers
                .stream()
                .map(user -> {
                    JwtToken token = user.getJwtToken();
                    Integer tokenId = token.getId();
                    return Long.valueOf(tokenId);
                })
                .toList();
        userRepository.deleteAllInBatch(expiredUsers);
        jwtTokenRepository.deleteAllByIdInBatch(expiredJwtTokens);
    }

    private boolean isTokenExpired(User user) {
        Optional<JwtToken> tokenTemplate = getAccessToken(user);
        if (tokenTemplate.isEmpty()) {
            return false;
        }
        JwtToken token = tokenTemplate.get();
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(3);
        return token.getCreated().isBefore(expirationTime);
    }

    private Optional<JwtToken> getAccessToken(User user) {
        JwtToken token = user.getJwtToken();
        return Optional.ofNullable(token);
    }
}