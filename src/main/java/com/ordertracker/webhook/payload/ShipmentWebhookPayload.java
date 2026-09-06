package com.ordertracker.webhook.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipmentWebhookPayload {

    @NotBlank(message = "Event ID boş ola bilməz")
    private String eventId;

    @NotNull(message = "Order ID boş ola bilməz")
    private Long orderId;

    @NotBlank(message = "Shipment status boş ola bilməz")
    private String shipmentStatus;

    @NotBlank(message = "Carrier boş ola bilməz")
    private String carrier;
}