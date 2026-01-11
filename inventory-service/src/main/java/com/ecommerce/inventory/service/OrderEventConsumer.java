package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received order created event for order ID: {}", event.getOrderId());
            inventoryService.processOrderCreated(event);
            log.info("Successfully processed order created event for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing order created event for order ID: {}", event.getOrderId(), e);
            throw e;
        }
    }
}