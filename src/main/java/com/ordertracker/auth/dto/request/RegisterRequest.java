package com.ordertracker.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Ad boş ola bilməz")
        String firstname,

        @NotBlank(message = "Soyad boş ola bilməz")
        String lastname,

        @NotBlank(message = "E-poçt boş ola bilməz")
        @Email(message = "Düzgün e-poçt formatı daxil edin")
        String email,

        @NotBlank(message = "Şifrə boş ola bilməz")
        @Size(min = 6, message = "Şifrə ən azı 6 simvol olmalıdır")
        String password
) {}