package com.ordertracker.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOrderStatusUpdateEmail(String toEmail, Long orderId, String newStatus) {
        try {
            sendEmail(
                    toEmail,
                    orderId,
                    newStatus
            );
        } catch (Exception e) {
            log.error(
                    "Email göndərilməsi uğursuz oldu. Email: {}, Order ID: {}",
                    toEmail,
                    orderId,
                    e
            );
        }
    }

    public void sendEmail(String toEmail, Long orderId, String newStatus) {
        log.info(
                "Sifariş status emaili göndərilir. Email: {}, Order ID: {}",
                toEmail,
                orderId
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order Status Update: " + orderId);
        message.setText(
                String.format(
                        "Hello,\n\n" +
                                "Your order (#%d) status has been updated to: %s.\n\n" +
                                "Thank you!",
                        orderId,
                        newStatus
                )
        );

        mailSender.send(message);

        log.info(
                "Sifariş status emaili uğurla göndərildi. Email: {}, Order ID: {}",
                toEmail,
                orderId
        );
    }
}