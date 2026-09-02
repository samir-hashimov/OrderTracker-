package com.ordertracker.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest(
        @NotEmpty(message = "Sifarişdə ən azı bir məhsul olmalıdır")
        @Valid
        List<OrderItemRequest> items
) {
}