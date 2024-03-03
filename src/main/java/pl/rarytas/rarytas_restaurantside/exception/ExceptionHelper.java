package pl.rarytas.rarytas_restaurantside.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Supplier;

@Component
public class ExceptionHelper {

    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();


    public ExceptionHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void throwLocalizedMessage(String messageCode, Object... args) throws LocalizedException {
        throw new LocalizedException(String.format(messageSource.getMessage(
                messageCode, args, locale)));
    }

    public Supplier<LocalizedException> supplyLocalizedMessage(String messageCode, Object... args) {
        return () -> new LocalizedException(String.format(messageSource.getMessage(
                messageCode, args, locale)));
    }
}
