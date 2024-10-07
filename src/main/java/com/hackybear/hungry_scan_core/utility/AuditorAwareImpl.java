package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.service.CustomUserDetails;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

@NonNullApi
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return Optional.of("anonymous");
        }

        String currentUserName = "unknown";
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            currentUserName = customUserDetails.getActiveProfile();
        }

        return Optional.ofNullable(currentUserName);
    }

}