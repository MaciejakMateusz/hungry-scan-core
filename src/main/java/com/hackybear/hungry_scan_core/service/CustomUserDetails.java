package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User implements UserDetails {

    private final String username;
    private final String password;
    private final Long activeRestaurantId;
    private final Long activeMenuId;
    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User byUsername, Collection<? extends GrantedAuthority> authorities) {
        this.username = byUsername.getUsername();
        this.password = byUsername.getPassword();
        this.activeRestaurantId = byUsername.getActiveRestaurantId();
        this.activeMenuId = byUsername.getActiveMenuId();
        this.authorities = authorities;
    }

}