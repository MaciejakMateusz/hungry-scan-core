package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.ForenameSurname;
import com.hackybear.hungry_scan_core.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Set;

public record UserDTO(@NotBlank
                      @ForenameSurname
                      String forename,

                      @NotBlank
                      @ForenameSurname
                      String surname,

                      @Email
                      @NotBlank
                      String username,

                      @NotEmpty
                      Set<OrganizationRestaurantDTO> restaurants,

                      @NotEmpty
                      Set<Role> roles,
                      boolean active) implements Serializable, Comparable<UserDTO> {

    @Override
    public int compareTo(UserDTO other) {
        return this.username.compareTo(other.username);
    }
}