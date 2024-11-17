package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class EmailServiceImp implements EmailService {

    private final JavaMailSender emailSender;
    private final UserMapper userMapper;

    @Value("${mail.no-reply}")
    private String noReplyMail;

    @Value("${mail.contact}")
    private String contactMail;

    @Value("${APP_URL}")
    private String applicationUrl;

    @Value("${CMS_APP_URL}")
    private String cmsUrl;

    public EmailServiceImp(JavaMailSender emailSender,
                           UserMapper userMapper) {
        this.emailSender = emailSender;
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
    public void passwordRecovery(String to, String emailToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(noReplyMail);
        message.setTo(to);
        message.setSubject("HungryScan - Jednorazowy link do zmiany hasła");

        String link = cmsUrl + "/recover/" + emailToken;
        message.setText("Zmień hasło: " + link);

        emailSender.send(message);
    }

    @Override
    public void activateAccount(String to, String emailToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(noReplyMail);
        message.setTo(to);
        message.setSubject("HungryScan - Link aktywacyjny konta");

        String link = applicationUrl + "/api/user/register/" + emailToken;
        message.setText("Aktywuj konto: " + link);

        emailSender.send(message);
    }
}