package com.notification.nofiticationservice.controller;

import com.notification.nofiticationservice.entity.Notification;
import com.notification.nofiticationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String message = (String) request.get("message");
        String type = (String) request.get("type");

        if (userId == null || message == null || message.trim().isEmpty() || type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing or invalid parameters: userId, message, or type");
        }

        notificationService.sendNotification(userId, message, type); // Truyền message gốc
        return ResponseEntity.ok("Notification sent and saved");
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Integer userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
}
