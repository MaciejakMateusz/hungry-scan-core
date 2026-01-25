package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@RequiredArgsConstructor
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

    @Override
    public void contactForm(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(noReplyMail);
        message.setReplyTo(from);
        message.setTo(contactMail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void passwordRecovery(String to, String emailToken) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(noReplyMail);
        helper.setTo(to);
        helper.setSubject("HungryScan - Jednorazowy link do zmiany hasła");

        String link = cmsUrl + "/new-password/?token=" + emailToken;
        String htmlContent = "<p>Aby zmienić hasło <a href='" + link + "'>kliknij tutaj</a>.</p>";
        helper.setText(htmlContent, true);

        emailSender.send(mimeMessage);
    }

    @Override
    public void activateAccount(String to, String emailToken) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(noReplyMail);
        helper.setTo(to);
        helper.setSubject("HungryScan - Link aktywacyjny konta");

        String htmlContent = getActivationHtmlContent(emailToken);
        helper.setText(htmlContent, true);
        emailSender.send(mimeMessage);
    }

    @Override
    public void accountCreated(String to, String tempPassword) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(noReplyMail);
        helper.setTo(to);
        helper.setSubject("HungryScan - Twoje konto zostało utworzone");

        String signInUrl = cmsUrl + "/sign-in";

        String htmlContent = """
                <p>Witaj!</p>
                <p>Twoje konto w systemie <b>HungryScan</b> zostało pomyślnie utworzone.</p>
                <p>Twoje tymczasowe hasło to:</p>
                <p style="font-size: 18px; font-weight: bold; color: #2c3e50;">%s</p>
                <p>Ze względów bezpieczeństwa prosimy, abyś jak najszybciej zmienił(a) to hasło
                po zalogowaniu się do swojego profilu użytkownika.</p>
                <p>Możesz zalogować się pod adresem: <a href="%s">%s</a></p>
                <br/>
                <p>Pozdrawiamy,<br/>Zespół HungryScan</p>
                """.formatted(tempPassword, signInUrl, signInUrl);

        helper.setText(htmlContent, true);
        emailSender.send(mimeMessage);
    }

    private String getActivationHtmlContent(String emailToken) {
        String activationLink = applicationUrl + "/api/user/register/" + emailToken;
        return """
                <p>Witaj!</p>
                <p>Dziękujemy za rejestrację w <b>HungryScan</b>. Aby aktywować swoje konto,
                prosimy <a href='%s'>kliknij tutaj</a>.</p>
                <p>Jeśli link nie działa, skopiuj i wklej go w pasku adresu przeglądarki:</p>
                <p>%s</p>
                <p>Miłego korzystania z systemu <b>HungryScan!</b></p>
                """.formatted(activationLink, activationLink);
    }
}