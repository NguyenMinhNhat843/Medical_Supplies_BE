package com.payment.paymentservice.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String createPaymentIntent(Long orderId, Long userId, long amount, String currency) throws StripeException {
        Stripe.apiKey = secretKey;

        // Tạo Stripe Customer với userId để lưu trữ thông tin thanh toán
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(" " + userId)
                .build();
        Customer customer = Customer.create(customerParams);

        // Metadata để xử lý webhook xác thực thanh toán
        Map<String, String> metadata = new HashMap<>();
        metadata.put("orderId", String.valueOf(orderId));
        metadata.put("userId", String.valueOf(userId));

        // Tạo PaymentIntent trả về stripeDashboard
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", List.of("card"));
        params.put("metadata", metadata);
        params.put("customer", customer.getId());

        PaymentIntent intent = PaymentIntent.create(params);
        System.out.println("Creating PaymentIntent with orderId: " + orderId + ", userId: " + userId);
        System.out.println("Metadata: " + metadata);
        return intent.getClientSecret();
    }
}
