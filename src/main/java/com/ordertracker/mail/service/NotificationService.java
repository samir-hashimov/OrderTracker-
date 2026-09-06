package com.ordertracker.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderStatusNotification(Long orderId, String newStatus) {
        String destination = "/topic/orders/" + orderId;

        Map<String, String> payload = Map.of(
                "orderId", orderId.toString(),
                "status", newStatus,
                "timestamp", LocalDateTime.now().toString()
        );

        messagingTemplate.convertAndSend(destination, payload);

        log.info(
                "Real-time WebSocket notification pushed to {}: {}",
                destination,
                newStatus
        );
    }
}