package com.ordertracker.auth.controller;

import com.ordertracker.auth.dto.request.LoginRequest;
import com.ordertracker.auth.dto.request.RefreshTokenRequest;
import com.ordertracker.auth.dto.request.RegisterRequest;
import com.ordertracker.auth.dto.response.AuthResponse;
import com.ordertracker.auth.dto.response.ErrorResponse;
import com.ordertracker.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication API",
        description = "İstifadəçi qeydiyyatı və sistemə giriş əməliyyatları"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Yeni istifadəçi qeydiyyatı",
            description = "Sistemə yeni istifadəçi əlavə edir və JWT token qaytarır."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Uğurlu qeydiyyat",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validasiya xətası",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Bu e-poçt artıq mövcuddur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "İstifadəçi girişi (Login)",
            description = "Mövcud istifadəçi e-poçt və şifrə ilə daxil olur, JWT token qaytarır."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Uğurlu giriş",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-poçt ünvanı və ya şifrə yanlışdır",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validasiya xətası",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Token yeniləmə (Refresh)",
            description = "Mövcud refresh token vasitəsilə yeni access token əldə edir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Yeni access token uğurla yaradıldı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Etibarsız və ya vaxtı bitmiş token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Yetkiləndirmə xətası",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
