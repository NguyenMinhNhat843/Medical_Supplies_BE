package com.payment.paymentservice.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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

    public String createPaymentIntent(Long orderId, long amount, String currency, Map<String, String> metadata) throws StripeException {
        Stripe.apiKey = secretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", List.of("card"));
        params.put("metadata", metadata);

        PaymentIntent intent = PaymentIntent.create(params);
        System.out.println("Creating PaymentIntent with orderId: " + orderId);
        System.out.println("Metadata: " + metadata);
        return intent.getClientSecret();
    }
}
