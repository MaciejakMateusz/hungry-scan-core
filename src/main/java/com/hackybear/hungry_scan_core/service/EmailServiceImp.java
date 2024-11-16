package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@Setter
public class EmailServiceImp implements EmailService {

    private final JavaMailSender emailSender;
    private final UserServiceImp userServiceImp;
    private final UserMapper userMapper;

    @Value("${mail.no-reply}")
    private String noReplyMail;

    @Value("${mail.contact}")
    private String contactMail;

    @Value("${APP_URL}")
    private String applicationUrl;

    public EmailServiceImp(JavaMailSender emailSender,
                           UserServiceImp userServiceImp,
                           UserMapper userMapper) {
        this.emailSender = emailSender;
        this.userServiceImp = userServiceImp;
        this.userMapper = userMapper;
    }

    @Override
    public void contactForm(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(contactMail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void passwordRecovery(String to) throws LocalizedException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(noReplyMail);
        message.setTo(to);
        message.setSubject("HungryScan - Jednorazowy link do zmiany hasła");

        User user = userServiceImp.findByUsername(to);
        user.setEmailToken(UUID.randomUUID().toString());
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        userServiceImp.update(registrationDTO);

        String link = applicationUrl + "/login/" + user.getEmailToken();
        message.setText("Zmień hasło: " + link);

        emailSender.send(message);
    }

    @Override
    public void activateAccount(String to) throws LocalizedException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(noReplyMail);
        message.setTo(to);
        message.setSubject("HungryScan - Link aktywacyjny konta");

        User user = userServiceImp.findByUsername(to);
        user.setEmailToken(UUID.randomUUID().toString());
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        userServiceImp.update(registrationDTO);

        String link = applicationUrl + "/register/" + user.getEmailToken();
        message.setText("Aktywuj konto: " + link);

        emailSender.send(message);
    }
}