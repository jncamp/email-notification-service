package com.example.notification.service;

import com.example.notification.entity.NotificationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailSenderService {

    private static final String FROM_EMAIL = "lovinmesomepop@yahoo.com";

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final TemplateEngine templateEngine;

    public EmailSenderService(JavaMailSender mailSender,
                              ObjectMapper objectMapper,
                              TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
        this.templateEngine = templateEngine;
    }

    public void send(NotificationRequest notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());

            String html = renderTemplate(notification);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to build/send HTML email", e);
        }
    }

    private String renderTemplate(NotificationRequest notification) {
        Map<String, Object> data = parseTemplateData(notification.getTemplateData());

        Context context = new Context();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        if (!data.containsKey("firstName")) {
            context.setVariable("firstName", "there");
        }

        return templateEngine.process(notification.getTemplateId(), context);
    }

    private Map<String, Object> parseTemplateData(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
