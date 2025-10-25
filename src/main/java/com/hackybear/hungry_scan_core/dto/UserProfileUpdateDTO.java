package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.ForenameSurname;
import com.hackybear.hungry_scan_core.annotation.Password;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record UserProfileUpdateDTO(@NotBlank
                                   @ForenameSurname
                                   String forename,

                                   @NotBlank
                                   @ForenameSurname
                                   String surname,

                                   String password,

                                   @Password
                                   String newPassword,

                                   String repeatedPassword) implements Serializable, Comparable<UserProfileUpdateDTO> {

    @Override
    public int compareTo(UserProfileUpdateDTO other) {
        return this.forename.concat(this.surname).compareTo(other.forename.concat(this.surname));
    }
}