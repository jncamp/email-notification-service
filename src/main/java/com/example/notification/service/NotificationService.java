package com.example.notification.service;

import com.example.notification.dto.CreateEmailNotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRequestRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class NotificationService {

    private final NotificationRequestRepository repository;

    public NotificationService(NotificationRequestRepository repository) {
        this.repository = repository;
    }

    public NotificationResponse createEmailNotification(CreateEmailNotificationRequest req) {

        OffsetDateTime now = OffsetDateTime.now();

        NotificationRequest n = new NotificationRequest();
        n.setRecipientEmail(req.getTo());
        n.setSubject(req.getSubject());
        n.setTemplateId(req.getTemplateId());
        n.setTemplateData(req.getTemplateData());
        n.setStatus(NotificationStatus.PENDING);
        n.setRetryCount(0);
        n.setLastError(null);
        n.setCreatedAt(now);
        n.setUpdatedAt(now);

        NotificationRequest saved = repository.save(n);

        return new NotificationResponse(
                saved.getId(),
                saved.getRecipientEmail(),
                saved.getSubject(),
                saved.getTemplateId(),
                saved.getStatus(),
                saved.getRetryCount(),
                saved.getLastError(),
                saved.getCreatedAt()
        );
    }

    public NotificationResponse getNotification(Long id) {

        NotificationRequest n = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found: " + id));

        return new NotificationResponse(
                n.getId(),
                n.getRecipientEmail(),
                n.getSubject(),
                n.getTemplateId(),
                n.getStatus(),
                n.getRetryCount(),
                n.getLastError(),
                n.getCreatedAt()
        );
    }
}
