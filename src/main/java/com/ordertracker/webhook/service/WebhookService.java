package com.ordertracker.webhook.service;

import com.ordertracker.exception.DuplicateWebhookException;
import com.ordertracker.exception.InvalidOrderStatusException;
import com.ordertracker.exception.OrderNotFoundException;
import com.ordertracker.mail.service.EmailService;
import com.ordertracker.mail.service.NotificationService;
import com.ordertracker.order.dao.entity.Order;
import com.ordertracker.order.dao.repository.OrderRepository;
import com.ordertracker.util.OrderStatus;
import com.ordertracker.webhook.dao.entity.ProcessedWebhook;
import com.ordertracker.webhook.dao.entity.WebhookLog;
import com.ordertracker.webhook.dao.repository.ProcessedWebhookRepository;
import com.ordertracker.webhook.dao.repository.WebhookLogRepository;
import com.ordertracker.webhook.payload.PaymentWebhookPayload;
import com.ordertracker.webhook.payload.ShipmentWebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final OrderRepository orderRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final ProcessedWebhookRepository processedWebhookRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Transactional
    public void processPaymentWebhook(PaymentWebhookPayload payload) {

        if (processedWebhookRepository.existsByEventId(payload.getEventId())) {
            throw new DuplicateWebhookException(
                    "Bu webhook artıq emal edilib. Event ID: " + payload.getEventId()
            );
        }

        logWebhook(
                "PAYMENT",
                payload.getEventId(),
                payload.toString(),
                "RECEIVED"
        );

        Order order = orderRepository.findById(payload.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Sifariş tapılmadı: " + payload.getOrderId()
                        )
                );

        if ("SUCCESS".equalsIgnoreCase(payload.getPaymentStatus())) {
            order.setStatus(OrderStatus.PAID);
        } else {
            throw new InvalidOrderStatusException(
                    "Ödəniş uğursuz olduğu üçün sifariş PAID statusuna keçirilə bilməz."
            );
        }

        orderRepository.save(order);

        processedWebhookRepository.save(
                ProcessedWebhook.builder()
                        .eventId(payload.getEventId())
                        .provider(payload.getProvider())
                        .build()
        );

        logWebhook(
                "PAYMENT",
                payload.getEventId(),
                payload.toString(),
                "PROCESSED"
        );

        log.info(
                "Payment webhook uğurla işləndi. Order ID: {}, Status: {}",
                order.getId(),
                order.getStatus()
        );

        notificationService.sendOrderStatusNotification(
                order.getId(),
                order.getStatus().name()
        );
    }

    @Transactional
    public void processShipmentWebhook(ShipmentWebhookPayload payload) {

        if (processedWebhookRepository.existsByEventId(payload.getEventId())) {
            log.warn("Webhook artıq işlənib. Event ID: {}", payload.getEventId());

            throw new DuplicateWebhookException(
                    "Bu webhook artıq emal edilib. Event ID: " + payload.getEventId()
            );
        }

        logWebhook(
                "SHIPMENT",
                payload.getEventId(),
                payload.toString(),
                "RECEIVED"
        );

        Order order = orderRepository.findById(payload.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Sifariş tapılmadı: " + payload.getOrderId()
                        )
                );

        OrderStatus newStatus;

        try {
            newStatus = OrderStatus.valueOf(
                    payload.getShipmentStatus().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException(
                    "Yanlış shipment status: " + payload.getShipmentStatus()
            );
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        processedWebhookRepository.save(
                ProcessedWebhook.builder()
                        .eventId(payload.getEventId())
                        .provider(payload.getCarrier())
                        .build()
        );

        logWebhook(
                "SHIPMENT",
                payload.getEventId(),
                payload.toString(),
                "PROCESSED"
        );

        log.info(
                "Shipment webhook uğurla işləndi. Order ID: {}, Status: {}",
                order.getId(),
                order.getStatus()
        );

        notificationService.sendOrderStatusNotification(
                order.getId(),
                order.getStatus().name()
        );
    }

    private void logWebhook(
            String type,
            String eventId,
            String payload,
            String status
    ) {
        WebhookLog logEntry = WebhookLog.builder()
                .type(type)
                .eventId(eventId)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .status(status)
                .build();

        webhookLogRepository.save(logEntry);
    }
}