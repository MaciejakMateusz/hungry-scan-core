package pl.rarytas.rarytas_restaurantside.service.interfaces;

public interface EmailService {
    void passwordRecovery(String to);

    void contactForm(String from, String subject, String text);

    void activateAccount(String to);
}