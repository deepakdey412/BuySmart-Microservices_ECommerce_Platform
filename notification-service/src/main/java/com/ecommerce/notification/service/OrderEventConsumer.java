package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received order created event for order ID: {}", event.getOrderId());
            notificationService.processOrderCreated(event);
            log.info("Successfully processed order created event for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing order created event for order ID: {}", event.getOrderId(), e);
        }
    }
}