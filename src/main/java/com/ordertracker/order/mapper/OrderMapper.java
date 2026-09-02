package com.ordertracker.order.mapper;

import com.ordertracker.order.dao.entity.Order;
import com.ordertracker.order.dao.entity.OrderItem;
import com.ordertracker.order.dto.request.OrderCreateRequest;
import com.ordertracker.order.dto.request.OrderItemRequest;
import com.ordertracker.order.dto.response.OrderItemResponse;
import com.ordertracker.order.dto.response.OrderResponse;
import com.ordertracker.util.OrderStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {OrderStatus.class, LocalDateTime.class, BigDecimal.class})
public interface OrderMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", expression = "java(OrderStatus.PENDING)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "items", source = "request.items")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    Order toOrder(OrderCreateRequest request, Long userId);

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse toOrderResponse(Order order);

    OrderItem toOrderItem(OrderItemRequest itemDto);

    OrderItemResponse toOrderItemResponse(OrderItem item);

    @AfterMapping
    default void finalizeOrderAndCalculateTotal(@MappingTarget Order order) {
        if (order.getItems() != null) {
            BigDecimal total = BigDecimal.ZERO;

            for (OrderItem item : order.getItems()) {
                item.setOrder(order);

                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }

            order.setTotalAmount(total);
        }
    }
}