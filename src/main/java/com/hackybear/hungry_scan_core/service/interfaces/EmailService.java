package com.hackybear.hungry_scan_core.service.interfaces;

public interface EmailService {

    void passwordRecovery(String to, String emailToken);

    void contactForm(String from, String subject, String text);

    void activateAccount(String to, String emailToken);
}