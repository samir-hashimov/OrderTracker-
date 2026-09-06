package com.ordertracker.auth.controller;

import com.ordertracker.auth.dto.response.ErrorResponse;
import com.ordertracker.auth.service.AdminService;
import com.ordertracker.util.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin API",
        description = "Sistem administratorları üçün xüsusi əməliyyatlar"
)
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "İstifadəçinin rolunu dəyiş",
            description = "Yalnız ADMIN səlahiyyətinə malik istifadəçinin başqa istifadəçinin rolunu dəyişməsinə imkan verir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "İstifadəçinin rolu uğurla dəyişdirildi"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "İstifadəçi onsuz da bu roldadır",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikasiya tələb olunur (Token yoxdur)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "İcazə rədd edildi (Yalnız ADMIN edə bilər)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "İstifadəçi tapılmadı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(
            @PathVariable Long userId,
            @RequestParam Role newRole
    ) {
        return ResponseEntity.ok(adminService.changeUserRole(userId, newRole));
    }
}