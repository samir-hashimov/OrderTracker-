package com.ordertracker.webhook.controller;

import com.ordertracker.auth.dto.response.ErrorResponse;
import com.ordertracker.webhook.payload.PaymentWebhookPayload;
import com.ordertracker.webhook.payload.ShipmentWebhookPayload;
import com.ordertracker.webhook.service.WebhookService;
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
@RequestMapping("/api/webhooks")
@Tag(
        name = "Webhook API",
        description = "Payment və shipment provider-lərindən gələn webhook-ların idarə edilməsi"
)
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/payment")
    @Operation(
            summary = "Payment webhook qəbul et",
            description = "Xarici payment provider-dən gələn ödəniş statusunu emal edir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment webhook uğurla emal edildi"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Webhook məlumatları yanlışdır",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sifariş tapılmadı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> handlePaymentWebhook(
            @Valid @RequestBody PaymentWebhookPayload payload
    ) {
        webhookService.processPaymentWebhook(payload);

        return ResponseEntity.ok("Payment webhook uğurla emal edildi.");
    }

    @PostMapping("/shipment")
    @Operation(
            summary = "Shipment webhook qəbul et",
            description = "Xarici carrier sistemindən gələn shipment statusunu emal edir."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Shipment webhook uğurla emal edildi"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Webhook məlumatları yanlışdır",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sifariş tapılmadı",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> handleShipmentWebhook(
            @Valid @RequestBody ShipmentWebhookPayload payload
    ) {
        webhookService.processShipmentWebhook(payload);

        return ResponseEntity.ok("Shipment webhook uğurla emal edildi.");
    }
}