package com.hackybear.hungry_scan_core.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ExceptionHelper {

    private final MessageSource messageSource;

    public void throwLocalizedMessage(String messageCode, Object... args) throws LocalizedException {
        Locale locale = LocaleContextHolder.getLocale();
        throw new LocalizedException(String.format(messageSource.getMessage(
                messageCode, args, locale)));
    }

    public Supplier<LocalizedException> supplyLocalizedMessage(String messageCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return () -> new LocalizedException(String.format(messageSource.getMessage(
                messageCode, args, locale)));
    }

    public String getLocalizedMsg(String messageCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return String.format(messageSource.getMessage(messageCode, args, locale));
    }
}
