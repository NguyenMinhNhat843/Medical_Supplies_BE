package com.payment.paymentservice.services.PaymentServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.payment.paymentservice.model.request.PaymentUpdateRequest;
import com.payment.paymentservice.model.response.OrderResponse;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StripeWebhookService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${order-service.url}")
    private String orderServiceUrl;

    public void handleChargeSucceeded(JsonNode chargeData) {
        try {
            String paymentIntentId = chargeData.get("payment_intent").asText();
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            String orderIdStr = intent.getMetadata().get("orderId");

            System.out.println("Đã thanh toán thành công cho orderId: " + orderIdStr);
            // Lấy orderId từ metadata của PaymentIntent
            Long orderId = Long.parseLong(orderIdStr);
            String updateUrl = orderServiceUrl + "/api/orders/" + orderId + "/payment-info";
            String url = orderServiceUrl + "/api/orders/" + orderId;
            ResponseEntity<OrderResponse> response = restTemplate.getForEntity(url, OrderResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Long userId = response.getBody().getCustomerId(); // userId từ order-service
                System.out.println("Đây là user đã thanh toán bằng VISA: userId = " + userId);
            }

            PaymentUpdateRequest update = new PaymentUpdateRequest();
            update.setPaymentMethod("VISA");
            update.setStatus("PENDING");
            update.setPaymentStatus("PAID");

            restTemplate.put(updateUrl, update);
            System.out.println("Đã cập nhật đơn hàng từ Stripe charge.succeeded");
        } catch (Exception e) {
            System.err.println("Lỗi trong StripeWebhookService: " + e.getMessage());
        }
    }
}
