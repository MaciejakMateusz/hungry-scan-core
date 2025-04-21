package com.hackybear.hungry_scan_core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class MessageSourceConfig {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames("classpath:messages", "classpath:validationMessages");
        ms.setDefaultEncoding("UTF-8");
        ms.setDefaultLocale(Locale.forLanguageTag("pl"));
        return ms;
    }
}