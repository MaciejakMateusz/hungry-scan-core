package com.hackybear.hungry_scan_core.service.interfaces;

import jakarta.mail.MessagingException;

public interface EmailService {

    void passwordRecovery(String to, String emailToken) throws MessagingException;

    void contactForm(String from, String subject, String text);

    void activateAccount(String to, String emailToken) throws MessagingException;

    void accountCreated(String to, String tempPassword) throws MessagingException;

}