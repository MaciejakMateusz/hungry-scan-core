package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public record RegistrationDTO(@NotBlank
                              String name,

                              @NotBlank
                              String surname,

                              @Email
                              @NotBlank
                              String username,

                              @Email
                              String email,

                              @NotBlank
                              @Password
                              String password,
                              String repeatedPassword) {
}