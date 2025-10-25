package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.entity.Role;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public record UserActivityDTO(String forename,
                              String surname,
                              String username,
                              Set<Role> roles,
                              LocalDateTime lastSeenAt,
                              boolean signedIn) implements Serializable, Comparable<UserActivityDTO> {

    @Override
    public int compareTo(UserActivityDTO other) {
        return this.username.compareTo(other.username);
    }
}