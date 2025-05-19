package com.notification.nofiticationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    public void sendPushNotification(String deviceToken, String title, String body) {
        Message message = Message.builder()
                .setToken(deviceToken)
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
        }
    }
}
