package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.Email;
import jakarta.validation.constraints.NotBlank;

public record RecoveryInitDTO(@NotBlank @Email String username) {
}