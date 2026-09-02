package com.ordertracker.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token boş ola bilməz")
        String refreshToken
) {}