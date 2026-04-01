package com.example.notification.dto;

import com.example.notification.entity.NotificationStatus;
import java.time.OffsetDateTime;

public class NotificationResponse {

    private final Long id;
    private final String to;
    private final String subject;
    private final String templateId;
    private final NotificationStatus status;
    private final Integer retryCount;
    private final String lastError;
    private final OffsetDateTime createdAt;

    public NotificationResponse(Long id,
                                String to,
                                String subject,
                                String templateId,
                                NotificationStatus status,
                                Integer retryCount,
                                String lastError,
                                OffsetDateTime createdAt) {
        this.id = id;
        this.to = to;
        this.subject = subject;
        this.templateId = templateId;
        this.status = status;
        this.retryCount = retryCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplateId() {
        return templateId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}