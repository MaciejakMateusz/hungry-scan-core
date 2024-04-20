package pl.rarytas.rarytas_restaurantside.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.JwtToken;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.JwtService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CustomerAccessRemover {

    private final JwtService jwtService;
    private final UserService userService;

    public CustomerAccessRemover(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 60000 * 60)
    @Transactional
    public void controlJwtAndRemoveUsers() {
        log.info("Running controlJwtAndRemoveUsers() job...");
        List<User> users = userService.findAll();
        for (User user : users) {
            String token = getAccessToken(user);
            if (token.isBlank()) continue;
            if (jwtService.isTokenExpired(token)) handleDeletion(user);
        }
        log.info("Finished controlJwtAndRemoveUsers() job.");
    }

    private String getAccessToken(User user) {
        JwtToken token = user.getJwtToken();
        return Objects.nonNull(token) ? token.getToken() : "";
    }

    private void handleDeletion(User user) {
        try {
            userService.delete(user.getId());
            log.info("CustomerAccessRemover - User {} deleted", user.getId());
        } catch (LocalizedException e) {
            log.error("CustomerAccessRemover - User {} could not be deleted", user.getId(), e);
        }
    }
}