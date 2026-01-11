package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.OrderCreatedEvent;
import com.ecommerce.inventory.dto.OrderItemDto;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.exception.InsufficientStockException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {
        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuntimeException("Inventory for product ID " + request.getProductId() + " already exists");
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(0);

        inventory = inventoryRepository.save(inventory);
        return mapToResponse(inventory);
    }

    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId.toString()));
        return mapToResponse(inventory);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id.toString()));

        inventory.setQuantity(request.getQuantity());

        inventory = inventoryRepository.save(inventory);
        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse reserveStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId.toString()));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product ID: " + productId);
        }

        int updated = inventoryRepository.reserveQuantity(productId, quantity);
        if (updated == 0) {
            throw new InsufficientStockException("Could not reserve stock for product ID: " + productId);
        }

        inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId.toString()));
        return mapToResponse(inventory);
    }

    @Transactional
    public void processOrderCreated(OrderCreatedEvent event) {
        for (OrderItemDto item : event.getItems()) {
            Inventory inventory = inventoryRepository.findByProductIdWithLock(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", item.getProductId().toString()));

            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product ID: " + item.getProductId());
            }

            int updated = inventoryRepository.reserveQuantity(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new InsufficientStockException("Could not reserve stock for product ID: " + item.getProductId());
            }
        }
    }

    @Transactional
    public void releaseReservedStock(Long productId, Integer quantity) {
        int updated = inventoryRepository.releaseReservedQuantity(productId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Could not release reserved stock for product ID: " + productId);
        }
    }

    @Transactional
    public void confirmAndDeductStock(Long productId, Integer quantity) {
        int updated = inventoryRepository.releaseAndDecreaseQuantity(productId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Could not confirm and deduct stock for product ID: " + productId);
        }
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}