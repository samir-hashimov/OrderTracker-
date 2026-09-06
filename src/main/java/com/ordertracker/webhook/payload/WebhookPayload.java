package com.ordertracker.webhook.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class WebhookPayload {

    @NotBlank(message = "Event ID boş ola bilməz")
    private String eventId;

    @NotBlank(message = "Event type boş ola bilməz")
    private String eventType;

    @NotNull(message = "Payload data boş ola bilməz")
    private Map<String, Object> data;
}