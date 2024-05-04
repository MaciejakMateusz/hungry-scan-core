package pl.rarytas.hungry_scan_core.service.interfaces;

public interface EmailService {
    void passwordRecovery(String to);

    void contactForm(String from, String subject, String text);

    void activateAccount(String to);
}