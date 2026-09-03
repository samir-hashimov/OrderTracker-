package com.ordertracker.mail.listener;

import com.ordertracker.auth.dao.entity.User;
import com.ordertracker.auth.dao.repository.UserRepository;
import com.ordertracker.exception.UserNotFoundException;
import com.ordertracker.mail.service.EmailService;
import com.ordertracker.mail.service.NotificationService;
import com.ordertracker.order.event.OrderUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleOrderUpdatedEvent(OrderUpdatedEvent event) {

        log.info(
                "[ASYNC] Order ID {} status changed from {} to {}",
                event.orderId(),
                event.oldStatus(),
                event.newStatus()
        );

        try {
            User user = userRepository.findById(event.userId())
                    .orElseThrow(() ->
                            new UserNotFoundException(
                                    "İstifadəçi tapılmadı: " + event.userId()
                            )
                    );

            emailService.sendOrderStatusUpdateEmail(
                    user.getEmail(),
                    event.orderId(),
                    event.newStatus().name()
            );

            notificationService.sendOrderStatusNotification(
                    event.orderId(),
                    event.newStatus().name()
            );

            log.info(
                    "[ASYNC] Notifications sent successfully for Order ID {}",
                    event.orderId()
            );

        } catch (Exception e) {
            log.error(
                    "[ASYNC] Notification failed for Order ID {}",
                    event.orderId(),
                    e
            );
        }
    }
}