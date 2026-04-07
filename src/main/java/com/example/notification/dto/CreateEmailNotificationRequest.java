package com.example.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import com.example.notification.validation.ValidJson;

public class CreateEmailNotificationRequest {

    @NotBlank(message = "to is required")
    @Email(message = "to must be a valid email address")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "to must contain a valid domain like example.com"
    )
    @Size(max = 320, message = "to must be 320 characters or less")
    private String to;

    @NotBlank(message = "subject is required")
    @Size(max = 255, message = "subject must be 255 characters or less")
    private String subject;

    @NotBlank(message = "templateId is required")
    @Pattern(
            regexp = "^(welcome-email|password-reset)$",
            message = "templateId must be one of: welcome-email, password-reset"
    )
    @Size(max = 100, message = "templateId must be 100 characters or less")
    private String templateId;

    @NotBlank(message = "templateData is required")
    @ValidJson(message = "templateData must be valid JSON")
    private String templateData;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(String templateData) {
        this.templateData = templateData;
    }
}
