package com.ordertracker.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-poçt boş ola bilməz")
        @Email(message = "Düzgün e-poçt formatı daxil edin")
        String email,

        @NotBlank(message = "Şifrə boş ola bilməz")
        String password
) {}