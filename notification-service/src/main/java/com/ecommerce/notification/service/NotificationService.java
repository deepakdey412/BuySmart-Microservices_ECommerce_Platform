package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.OrderCreatedEvent;
import com.ecommerce.notification.entity.NotificationLog;
import com.ecommerce.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationLogRepository notificationLogRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendOrderConfirmationEmail(Long userId, Long orderId, String userEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Order Confirmation - Order #" + orderId);
            message.setText("Thank you for your order! Your order #" + orderId + " has been confirmed.");

            mailSender.send(message);

            NotificationLog log = new NotificationLog();
            log.setUserId(userId);
            log.setType("ORDER_CONFIRMATION");
            log.setRecipient(userEmail);
            log.setSubject("Order Confirmation - Order #" + orderId);
            log.setBody(message.getText());
            log.setStatus(NotificationLog.Status.SENT);

            notificationLogRepository.save(log);
//            log.info("Order confirmation email sent successfully to user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to user ID: {}", userId, e);

            NotificationLog log = new NotificationLog();
            log.setUserId(userId);
            log.setType("ORDER_CONFIRMATION");
            log.setRecipient(userEmail);
            log.setSubject("Order Confirmation - Order #" + orderId);
            log.setStatus(NotificationLog.Status.FAILED);
            log.setErrorMessage(e.getMessage());

            notificationLogRepository.save(log);
        }
    }

    @Transactional
    public void processOrderCreated(OrderCreatedEvent event) {
        // In a real scenario, we would fetch user email from user service
        String userEmail = "user" + event.getUserId() + "@example.com"; // Placeholder
        sendOrderConfirmationEmail(event.getUserId(), event.getOrderId(), userEmail);
    }
}