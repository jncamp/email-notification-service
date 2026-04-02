package com.example.notification.service;

import com.example.notification.entity.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmailSenderServiceTest {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);

        ObjectMapper objectMapper = new ObjectMapper();

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>Hello John</body></html>");

        emailSenderService =
                new EmailSenderService(mailSender, objectMapper, templateEngine);
    }

    @Test
    void sendsHtmlEmailUsingTemplate() {
        NotificationRequest notification = new NotificationRequest();
        notification.setRecipientEmail("j_n_camp@hotmail.com");
        notification.setSubject("Welcome aboard");
        notification.setTemplateId("welcome-email");
        notification.setTemplateData("{\"firstName\":\"John\"}");

        emailSenderService.send(notification);

        verify(templateEngine).process(eq("welcome-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }
}
