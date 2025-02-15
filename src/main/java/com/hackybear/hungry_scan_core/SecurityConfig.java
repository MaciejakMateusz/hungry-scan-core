package com.hackybear.hungry_scan_core;

import com.hackybear.hungry_scan_core.filter.JwtAuthFilter;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;
import java.util.Set;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final Environment env;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(Environment env, JwtAuthFilter jwtAuthFilter) {
        this.env = env;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector hmi) throws Exception {

        MvcRequestMatcher.Builder mvcMatcherBuilder =
                new MvcRequestMatcher
                        .Builder(hmi);

        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(c -> c.configurationSource(req -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedMethods(List.of("*"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.addAllowedOrigin(env.getProperty("JENKINS_URL"));
            corsConfiguration.addAllowedOrigin(env.getProperty("CUSTOMER_APP_URL"));
            corsConfiguration.addAllowedOrigin(env.getProperty("CMS_APP_URL"));
            corsConfiguration.setAllowedOriginPatterns(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));


        http.authorizeHttpRequests(
                c -> c.dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/order-websocket/**")).permitAll()
                        .requestMatchers(PathRequest.toStaticResources()
                                .at(Set.of(StaticResourceLocation.IMAGES))).permitAll()
        );

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}