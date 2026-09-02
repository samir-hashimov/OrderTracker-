package com.ordertracker.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}