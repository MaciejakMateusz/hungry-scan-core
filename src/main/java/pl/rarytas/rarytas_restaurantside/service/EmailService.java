package pl.rarytas.rarytas_restaurantside.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.interfaces.EmailServiceInterface;

import java.util.Objects;
import java.util.UUID;

@Component
@Getter
@Setter
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender emailSender;
    private final UserService userService;
    private static final String LOCAL_HOST = "http://localhost:8080/";

    public EmailService(JavaMailSender emailSender,
                        UserService userService) {
        this.emailSender = emailSender;
        this.userService = userService;
    }

    @Override
    public void contactForm(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo("applicationcharity@gmail.com");
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void passwordRecovery(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@charityapp.pl");
        message.setTo(to);
        message.setSubject("Charity - jednorazowy link do zmiany hasła");

        User user = userService.findByUsername(to);
        user.setToken(UUID.randomUUID().toString());
        userService.update(user);

        String link = determineBaseUrl() + "/login/" + user.getToken();
        message.setText("Twój link do zmiany hasła: " + link);

        emailSender.send(message);
    }

    @Override
    public void activateAccount(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@charityapp.pl");
        message.setTo(to);
        message.setSubject("Charity - link aktywacyjny do konta");

        User user = userService.findByUsername(to);
        user.setToken(UUID.randomUUID().toString());
        userService.update(user);

        String link = determineBaseUrl() + "/register/" + user.getToken();
        message.setText("Kliknij w ten link, aby aktywować swoje konto: " + link);

        emailSender.send(message);
    }

    private String determineBaseUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestUrl = request.getRequestURL().toString();
        return requestUrl.replace(request.getRequestURI(), request.getContextPath());
    }
}