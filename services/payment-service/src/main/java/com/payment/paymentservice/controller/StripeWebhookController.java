package com.payment.paymentservice.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.request.PaymentUpdateRequest;
import com.payment.paymentservice.repository.PaymentRepository;
import com.payment.paymentservice.services.PaymentServiceImpl.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${order-service.url}")
    private String orderServiceUrl;

    @Autowired
    private StripeWebhookService stripeWebhookService;
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            //  Xác thực chữ ký webhook
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Xử lý từng loại sự kiện
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent succeededIntent = (PaymentIntent)
                            event.getDataObjectDeserializer().getObject().orElse(null);
                    if (succeededIntent != null) {
                        System.out.println("Payment succeeded: " + succeededIntent.getId());

                    }
                    break;

                case "payment_intent.created":
                    System.out.println("PaymentIntent vừa được tạo.");
                    break;

                case "charge.succeeded":
                    System.out.println("Thanh toán đã thành công.");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode chargeData = mapper.readTree(event.getData().getObject().toJson());
                    stripeWebhookService.handleChargeSucceeded(chargeData);
                    break;

                default:
                    System.out.println("ℹSự kiện khác: " + event.getType());
            }

            return ResponseEntity.ok("Received");
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook signature sai: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception ex) {
            System.err.println("Lỗi webhook xử lý: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook error");
        }
    }
}