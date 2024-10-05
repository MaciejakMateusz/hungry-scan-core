package com.hackybear.hungry_scan_core.utility;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

@NonNullApi
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = "unknown";
        if (!(authentication instanceof AnonymousAuthenticationToken) && Objects.nonNull(authentication)) {
            currentUserName = authentication.getName();
        }
        return Optional.of(currentUserName);
    }

}