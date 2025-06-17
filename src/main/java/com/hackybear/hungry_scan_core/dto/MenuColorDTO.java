package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

public record MenuColorDTO(Long id,

                           @NotBlank String hex) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}