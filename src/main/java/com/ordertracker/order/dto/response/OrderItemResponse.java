package com.ordertracker.order.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}