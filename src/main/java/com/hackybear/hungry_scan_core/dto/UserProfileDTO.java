package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.entity.Role;

import java.io.Serializable;
import java.util.Set;

public record UserProfileDTO(String forename,
                             String surname,
                             String username,
                             Set<Role> roles,
                             Long organizationId) implements Serializable, Comparable<UserProfileDTO> {

    @Override
    public int compareTo(UserProfileDTO other) {
        return this.username.compareTo(other.username);
    }
}