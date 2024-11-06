package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;

public interface EmailService {
    void passwordRecovery(String to) throws LocalizedException;

    void contactForm(String from, String subject, String text);

    void activateAccount(String to) throws LocalizedException;
}