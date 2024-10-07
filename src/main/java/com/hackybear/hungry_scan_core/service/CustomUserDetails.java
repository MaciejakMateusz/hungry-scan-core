package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Profile;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails extends User implements UserDetails {

    private final String username;
    private final String password;
    private final String activeProfile;
    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User byUsername) {
        this.username = byUsername.getUsername();
        this.password = byUsername.getPassword();
        this.activeProfile = getActiveProfile(byUsername);
        List<GrantedAuthority> auths = new ArrayList<>();

        for (Role role : byUsername.getRoles()) {

            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    private String getActiveProfile(User user) {
        return user.getProfiles().stream()
                .filter(Profile::isActive)
                .map(Profile::getName)
                .findFirst()
                .orElse("anonymous");
    }
}