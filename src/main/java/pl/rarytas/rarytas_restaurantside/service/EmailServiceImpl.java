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
import pl.rarytas.rarytas_restaurantside.service.interfaces.EmailService;

import java.util.Objects;
import java.util.UUID;

@Component
@Getter
@Setter
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final UserServiceImpl userServiceImpl;
    private static final String LOCAL_HOST = "http://localhost:8080/";

    public EmailServiceImpl(JavaMailSender emailSender,
                            UserServiceImpl userServiceImpl) {
        this.emailSender = emailSender;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void contactForm(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo("restauracjararytas@gmail.com");
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void passwordRecovery(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@restauracjararytas.pl");
        message.setTo(to);
        message.setSubject("Restauracja Rarytas - jednorazowy link do zmiany hasła");

        User user = userServiceImpl.findByUsername(to);
        user.setToken(UUID.randomUUID().toString());
        userServiceImpl.update(user);

        String link = determineBaseUrl() + "/login/" + user.getToken();
        message.setText("Twój link do zmiany hasła: " + link);

        emailSender.send(message);
    }

    @Override
    public void activateAccount(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@restauracjararytas.pl");
        message.setTo(to);
        message.setSubject("Restauracja Rarytas - link aktywacyjny do konta");

        User user = userServiceImpl.findByUsername(to);
        user.setToken(UUID.randomUUID().toString());
        userServiceImpl.update(user);

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