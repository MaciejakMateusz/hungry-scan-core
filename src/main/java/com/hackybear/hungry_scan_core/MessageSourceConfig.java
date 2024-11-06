package com.hackybear.hungry_scan_core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        return getMessageSource("messages");
    }

    @Bean
    public ReloadableResourceBundleMessageSource validationMessageSource() {
        return getMessageSource("validationMessages");
    }

    private ReloadableResourceBundleMessageSource getMessageSource(String bundleName) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:" + bundleName);
        messageSource.setDefaultLocale(Locale.forLanguageTag("pl"));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}