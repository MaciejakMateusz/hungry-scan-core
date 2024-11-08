package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;

@Component
@Getter
@Setter
public class EmailServiceImp implements EmailService {

    private final JavaMailSender emailSender;
    private final UserServiceImp userServiceImp;
    private final UserMapper userMapper;
    private static final String LOCAL_HOST = "http://localhost:8080/";

    public EmailServiceImp(JavaMailSender emailSender,
                           UserServiceImp userServiceImp, UserMapper userMapper) {
        this.emailSender = emailSender;
        this.userServiceImp = userServiceImp;
        this.userMapper = userMapper;
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
    public void passwordRecovery(String to) throws LocalizedException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@restauracjararytas.pl");
        message.setTo(to);
        message.setSubject("Restauracja Rarytas - jednorazowy link do zmiany hasła");

        User user = userServiceImp.findByUsername(to);
        user.setEmailToken(UUID.randomUUID().toString());
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        userServiceImp.update(registrationDTO);

        String link = determineBaseUrl() + "/login/" + user.getEmailToken();
        message.setText("Twój link do zmiany hasła: " + link);

        emailSender.send(message);
    }

    @Override
    public void activateAccount(String to) throws LocalizedException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@restauracjararytas.pl");
        message.setTo(to);
        message.setSubject("Restauracja Rarytas - link aktywacyjny do konta");

        User user = userServiceImp.findByUsername(to);
        user.setEmailToken(UUID.randomUUID().toString());
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        userServiceImp.update(registrationDTO);

        String link = determineBaseUrl() + "/register/" + user.getEmailToken();
        message.setText("Kliknij w ten link, aby aktywować swoje konto: " + link);

        emailSender.send(message);
    }

    private String determineBaseUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestUrl = request.getRequestURL().toString();
        return requestUrl.replace(request.getRequestURI(), request.getContextPath());
    }
}