package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Password;
import jakarta.validation.constraints.NotBlank;

public record RecoveryDTO(@NotBlank
                          String emailToken,

                          @NotBlank
                          @Password
                          String password,

                          String repeatedPassword) {
}