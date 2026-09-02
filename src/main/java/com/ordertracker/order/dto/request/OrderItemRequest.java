package com.ordertracker.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemRequest(
        @NotBlank(message = "Məhsulun adı boş ola bilməz")
        String productName,

        @NotNull(message = "Say qeyd olunmalıdır")
        @Min(value = 1, message = "Say ən azı 1 olmalıdır")
        Integer quantity,

        @NotNull(message = "Qiymət qeyd olunmalıdır")
        @Min(value = 0, message = "Qiymət mənfi ola bilməz")
        BigDecimal unitPrice
) {}