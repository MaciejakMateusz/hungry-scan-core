package pl.rarytas.rarytas_restaurantside;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.rarytas.rarytas_restaurantside.filter.AdminFilter;
import pl.rarytas.rarytas_restaurantside.filter.AuthFilter;

import java.util.Collections;

@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterFilterRegistration() {
        final FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter());
        registrationBean.setName("AuthFilter");
        registrationBean.setUrlPatterns(Collections.singletonList("/restaurant/*"));
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AdminFilter> adminFilterFilterRegistration() {
        final FilterRegistrationBean<AdminFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AdminFilter());
        registrationBean.setName("AdminFilter");
        registrationBean.setUrlPatterns(Collections.singletonList("/restaurant/cms/*"));
        return registrationBean;
    }
}
