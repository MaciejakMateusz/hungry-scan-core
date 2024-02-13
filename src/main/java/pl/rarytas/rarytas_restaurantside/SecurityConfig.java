package pl.rarytas.rarytas_restaurantside;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import pl.rarytas.rarytas_restaurantside.filter.JwtAuthFilter;
import pl.rarytas.rarytas_restaurantside.utility.LoginSuccessHandler;

import java.util.List;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {

        MvcRequestMatcher.Builder mvcMatcherBuilder =
                new MvcRequestMatcher
                        .Builder(handlerMappingIntrospector);


        http.csrf(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(c -> c.configurationSource(req -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedMethods(List.of("*"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.addAllowedOrigin("http://localhost:8080");
            corsConfiguration.addAllowedOrigin("http://localhost:3000");
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));


        http.authorizeHttpRequests(
                c -> c.dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/public/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/webjars/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/api/**")).permitAll()
                        .requestMatchers(mvcMatcherBuilder.pattern("/order-websocket/**")).permitAll()
                        .requestMatchers(
                                mvcMatcherBuilder.pattern("/restaurant"),
                                mvcMatcherBuilder.pattern("/restaurant/orders/**"),
                                mvcMatcherBuilder.pattern("/restaurant/menu"),
                                mvcMatcherBuilder.pattern("/restaurant/bookings"),
                                mvcMatcherBuilder.pattern("/restaurant/finalize-dineIn"),
                                mvcMatcherBuilder.pattern("/restaurant/finalize-takeAway")).hasAnyRole("WAITER", "COOK", "MANAGER", "ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/restaurant/cms/**")).hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(mvcMatcherBuilder.pattern("/register/**"))
                        .access(new WebExpressionAuthorizationManager("isAnonymous()"))
                        .anyRequest().anonymous()
        );

        http.formLogin(login ->
                login.loginPage("/login")
                        .defaultSuccessUrl("/restaurant")
                        .successHandler(new LoginSuccessHandler())
                        .permitAll()
        );

        http.logout((logout) -> logout.logoutUrl("/restaurant/logout").permitAll());

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

}