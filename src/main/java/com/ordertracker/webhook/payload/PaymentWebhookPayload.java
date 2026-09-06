package com.ordertracker.webhook.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentWebhookPayload {

    @NotBlank(message = "Event ID boş ola bilməz")
    private String eventId;

    @NotNull(message = "Order ID boş ola bilməz")
    private Long orderId;

    @NotBlank(message = "Payment status boş ola bilməz")
    private String paymentStatus;

    @NotBlank(message = "Provider boş ola bilməz")
    private String provider;
}