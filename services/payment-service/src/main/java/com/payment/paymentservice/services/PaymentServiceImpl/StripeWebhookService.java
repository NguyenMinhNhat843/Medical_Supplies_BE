package com.payment.paymentservice.services.PaymentServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.payment.paymentservice.model.request.PaymentUpdateRequest;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

            Long orderId = Long.parseLong(orderIdStr);
            String url = orderServiceUrl + "/api/orders/" + orderId + "/payment-info";

            PaymentUpdateRequest update = new PaymentUpdateRequest();
            update.setPaymentMethod("VISA");
            update.setStatus("PENDING");
            update.setPaymentStatus("PAID");

            restTemplate.put(url, update);
            System.out.println("Đã cập nhật đơn hàng từ Stripe charge.succeeded");
        } catch (Exception e) {
            System.err.println("Lỗi trong StripeWebhookService: " + e.getMessage());
        }
    }
}
