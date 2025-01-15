package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.ForenameSurname;
import com.hackybear.hungry_scan_core.annotation.Password;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record RegistrationDTO(@NotBlank
                              @ForenameSurname
                              String forename,

                              @NotBlank
                              @ForenameSurname
                              String surname,

                              @Email
                              @NotBlank
                              String username,

                              @Email
                              String email,

                              @NotBlank
                              @Password
                              String password,
                              String repeatedPassword) implements Serializable, Comparable<RegistrationDTO> {

    @Override
    public int compareTo(RegistrationDTO other) {
        return this.surname.compareTo(other.surname);
    }
}