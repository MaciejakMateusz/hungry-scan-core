package com.hackybear.hungry_scan_core;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class LocaleResolver extends AcceptHeaderLocaleResolver {

    private static final List<Locale> LOCALES = Arrays.asList(
            Locale.of("pl"),
            Locale.of("en"),
            Locale.of("de"),
            Locale.of("es"),
            Locale.of("fr"),
            Locale.of("uk")
    );
    private static final Locale FALLBACK_LOCALE = Locale.of("pl");

    @Override
    @NonNull
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");

        if (Objects.isNull(headerLang) || headerLang.isEmpty()) {
            return FALLBACK_LOCALE;
        }

        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(headerLang);
        Locale matchedLocale = Locale.lookup(languageRanges, LOCALES);

        return matchedLocale != null ? matchedLocale : FALLBACK_LOCALE;
    }
}
