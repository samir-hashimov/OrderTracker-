package com.ordertracker.order.service;

import com.ordertracker.exception.DuplicateOrderException;
import com.ordertracker.exception.InvalidOrderStatusException;
import com.ordertracker.exception.InvalidStatusTransitionException;
import com.ordertracker.exception.OrderNotFoundException;
import com.ordertracker.order.dao.entity.Order;
import com.ordertracker.order.dao.entity.OrderItem;
import com.ordertracker.order.dao.repository.OrderRepository;
import com.ordertracker.order.dto.request.OrderCreateRequest;
import com.ordertracker.order.dto.request.OrderItemRequest;
import com.ordertracker.order.dto.response.OrderResponse;
import com.ordertracker.order.mapper.OrderMapper;
import com.ordertracker.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request, Long userId) {
        Optional<Order> lastOrderOpt = orderRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

        if (lastOrderOpt.isPresent()) {
            Order lastOrder = lastOrderOpt.get();

            if (lastOrder.getStatus() == OrderStatus.PENDING) {
                boolean isDuplicate = checkDuplicateItems(request.items(), lastOrder.getItems());
                if (isDuplicate) {
                    throw new DuplicateOrderException("Siz artıq eyni məhsullarla sifariş yaratmısınız! Zəhmət olmasa fərqli məhsul seçin.");
                }
            }
        }

        Order order = orderMapper.toOrder(request, userId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository
                .findByUserId(userId, pageable)
                .map(orderMapper::toOrderResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Sifariş tapılmadı!"));

        OrderStatus requestedStatus;
        try {
            requestedStatus = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException(
                    "Yanlış status! " +
                            "İcazə verilənlər: PENDING, PAID, SHIPPED, COMPLETED, CANCELLED"
            );
        }

        validateStatusTransition(order.getStatus(), requestedStatus);

        order.setStatus(requestedStatus);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Sifariş tapılmadı və ya bu sifariş sizə aid deyil!"
                ));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Sifariş onsuz da ləğv edilib.");
        }

        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidStatusTransitionException(
                    "Ödənilmiş, yola çıxmış və ya tamamlanmış " +
                            "sifarişləri ləğv etmək mümkün deyil!");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public byte[] exportAllOrdersToCsv() {
        List<Order> orders = orderRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("Sifaris ID,Istifadeci ID,Status,Mebleg,Tarix\n");

        for (Order order : orders) {
            csvBuilder.append(order.getId()).append(",")
                    .append(order.getUserId()).append(",")
                    .append(order.getStatus().name()).append(",")
                    .append(order.getTotalAmount()).append(",")
                    .append(order.getCreatedAt())
                    .append("\n");
        }

        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);

        byte[] finalCsv = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, finalCsv, 0, bom.length);
        System.arraycopy(csvBytes, 0, finalCsv, bom.length, csvBytes.length);

        return finalCsv;
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            throw new InvalidStatusTransitionException("Sifariş artıq " + current + " statusundadır.");
        }

        boolean isValid = switch (current) {
            case PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!isValid) {
            throw new InvalidStatusTransitionException(
                    String.format(
                            "Sifariş statusunu %s statusundan %s statusuna dəyişmək mümkün deyil!",
                            current,
                            next
                    )
            );
        }
    }

    private boolean checkDuplicateItems(List<OrderItemRequest> newItems, List<OrderItem> existingItems) {
        if (newItems.size() != existingItems.size()) {
            return false;
        }

        List<String> newItemsList = newItems.stream()
                .map(item -> item.productName() + "-" + item.quantity())
                .sorted()
                .toList();

        List<String> existingItemsList = existingItems.stream()
                .map(item -> item.getProductName() + "-" + item.getQuantity())
                .sorted()
                .toList();

        return newItemsList.equals(existingItemsList);
    }
}