package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.ForenameSurname;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record UserProfileDTO(@NotBlank
                             @ForenameSurname
                             String forename,

                             @NotBlank
                             @ForenameSurname
                             String surname,

                             @Email
                             @NotBlank
                             String username) implements Serializable, Comparable<UserProfileDTO> {

    @Override
    public int compareTo(UserProfileDTO other) {
        return this.surname.compareTo(other.surname);
    }
}