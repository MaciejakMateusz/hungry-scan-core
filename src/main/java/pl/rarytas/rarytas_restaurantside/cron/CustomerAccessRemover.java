package pl.rarytas.rarytas_restaurantside.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.JwtToken;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CustomerAccessRemover {

    private final UserService userService;
    private final SettingsService settingsService;

    public CustomerAccessRemover(UserService userService, SettingsService settingsService) {
        this.userService = userService;
        this.settingsService = settingsService;
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
        Settings settings = settingsService.getSettings();
        Long sessionTime = settings.getCustomerSessionTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.minusHours(sessionTime);
        return token.getCreated().isBefore(expirationTime);
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