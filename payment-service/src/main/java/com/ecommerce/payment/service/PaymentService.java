package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.CreatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.exception.ResourceNotFoundException;
import com.ecommerce.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key:sk_test_51YourStripeSecretKey}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public PaymentResponse processPayment(Long userId, CreatePaymentRequest request) {
        try {
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount((long) (request.getAmount().doubleValue() * 100)) // Convert to cents
                    .setCurrency("usd")
                    .setSource(request.getToken())
                    .setDescription("Payment for order #" + request.getOrderId())
                    .build();

            Charge charge = Charge.create(params);

            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(userId);
            payment.setAmount(request.getAmount());
            payment.setTransactionId(charge.getId());
            payment.setPaymentMethod("card");
            
            if (charge.getStatus().equals("succeeded")) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
            }

            payment = paymentRepository.save(payment);
            log.info("Payment processed successfully for order ID: {}", request.getOrderId());
            
            return mapToResponse(payment);
        } catch (StripeException e) {
            log.error("Stripe payment error: ", e);
            
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setUserId(userId);
            payment.setAmount(request.getAmount());
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id.toString()));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId.toString()));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}