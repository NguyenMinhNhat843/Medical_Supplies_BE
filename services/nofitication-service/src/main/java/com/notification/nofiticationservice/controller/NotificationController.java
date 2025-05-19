package com.notification.nofiticationservice.controller;

import com.notification.nofiticationservice.entity.Notification;
import com.notification.nofiticationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public String sendNotification(@RequestParam Integer userId,
                                   @RequestParam String message,
                                   @RequestParam String type) {
        notificationService.sendNotification(userId, message, type);
        return "Notification sent and saved";
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Integer userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
}
