package com.ordertracker.order.event;

import com.ordertracker.util.OrderStatus;

public record OrderUpdatedEvent(
        Long orderId,
        Long userId,
        OrderStatus oldStatus,
        OrderStatus newStatus
) {
}