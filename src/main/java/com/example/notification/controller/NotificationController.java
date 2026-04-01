package com.example.notification.controller;

import com.example.notification.dto.CreateEmailNotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping("/{id}")
    public NotificationResponse getNotification(@PathVariable Long id) {
        return notificationService.getNotification(id);
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse createEmailNotification(
            @Valid @RequestBody CreateEmailNotificationRequest request) {
        return notificationService.createEmailNotification(request);
    }
}
