package com.ordertracker.order.controller;

import com.ordertracker.auth.dto.response.ErrorResponse;
import com.ordertracker.order.dto.request.OrderCreateRequest;
import com.ordertracker.order.dto.response.OrderResponse;
import com.ordertracker.order.service.OrderService;
import com.ordertracker.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "Sifarişlərin idarə edilməsi")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Yeni sifariş yarat",
            description = "Yeni sifariş formalaşdırır (Spam və təkrarlanma nəzarəti ilə)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Sifariş uğurla yaradıldı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validasiya xətası, spam və ya təkrarlanan məhsullar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401",
                    description = "Autentifikasiya tələb olunur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, currentUser.getId()));
    }

    @Operation(
            summary = "Sifarişlərimi gətir (Səhifələnmiş)",
            description = "Sistemə daxil olmuş istifadəçinin tarixçəsini ən yenidən köhnəyə doğru gətirir"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sifarişlər uğurla gətirildi",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikasiya tələb olunur",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getUserOrders(currentUser.getId(), pageable));
    }

    @Operation(
            summary = "Sifarişin statusunu yenilə (Yalnız ADMIN)",
            description = "Sifarişin mövcud statusunu State Machine qaydalarına əsasən yeniləyir"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status uğurla yeniləndi",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Yanlış status formatı və ya icazə verilməyən status ardıcıllığı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikasiya tələb olunur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "İcazə rədd edildi (Yalnız ADMIN edə bilər)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sifariş tapılmadı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @Operation(
            summary = "Sifarişi ləğv et",
            description = "Yalnız PENDING statusunda olan sifarişləri ləğv etmək mümkündür"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Sifariş uğurla ləğv edildi"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ödənilmiş, yola çıxmış və ya tamamlanmış sifarişi ləğv etmək olmaz",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikasiya tələb olunur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sifariş tapılmadı və ya sizə aid deyil",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        orderService.cancelOrder(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Sifarişləri CSV formatında yüklə (Yalnız ADMIN)",
            description = "Bütün sifarişləri Excel-də açıla bilən CSV formatında çıxarır"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "CSV faylı uğurla formalaşdırıldı və yüklənildi"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Autentifikasiya tələb olunur",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "İcazə rədd edildi (Yalnız ADMIN edə bilər)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportOrders() {

        byte[] csvData = orderService.exportAllOrdersToCsv();

        HttpHeaders headers = new HttpHeaders();

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=butun_sifarisler.csv"
        );

        headers.setContentType(
                MediaType.parseMediaType("text/csv")
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}