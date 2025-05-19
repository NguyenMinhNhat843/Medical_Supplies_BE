package com.notification.nofiticationservice.service;

import com.notification.nofiticationservice.entity.Notification;
import com.notification.nofiticationservice.entity.User;
import com.notification.nofiticationservice.repository.NotificationRepository;
import com.notification.nofiticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(Integer userId, String message, String type) {
        // Lưu thông báo vào database
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        // Tìm thông tin người dùng
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getEmail() != null) {
            // Gửi email
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject("Thông báo từ Shop Medical_Supplies: ");
            email.setText(message);
            email.setFrom("nhanmpt000@gmail.com");

            try {
                mailSender.send(email);
                System.out.println("Email sent to: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("Error sending email: " + e.getMessage());
            }
        } else {
            System.err.println("User or email not found for userId: " + userId);
        }
    }

    public List<Notification> getNotificationsByUserId(Integer userId) {
        return notificationRepository.findByUserId(userId);
    }
}