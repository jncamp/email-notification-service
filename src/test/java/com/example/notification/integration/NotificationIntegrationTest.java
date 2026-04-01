package com.example.notification.integration;

import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRequestRepository;
import com.example.notification.service.NotificationWorker;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class NotificationIntegrationTest {

    @Autowired
    private NotificationRequestRepository repository;

    @Autowired
    private NotificationWorker worker;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void workerProcessesPendingNotification() {
        // mock mail
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // create test data
        NotificationRequest n = new NotificationRequest();
        OffsetDateTime now = OffsetDateTime.now();

        n.setRecipientEmail("test@example.com");
        n.setSubject("Integration test");
        n.setTemplateId("welcome-email");
        n.setTemplateData("{\"firstName\":\"John\"}");
        n.setStatus(NotificationStatus.PENDING);
        n.setRetryCount(0);
        n.setCreatedAt(now);
        n.setUpdatedAt(now);

        n = repository.save(n);

        // run worker
        worker.processPendingNotifications();

        // verify DB update
        Optional<NotificationRequest> updated = repository.findById(n.getId());

        assertTrue(updated.isPresent());
        assertEquals(NotificationStatus.SENT, updated.get().getStatus());

        // verify email send was attempted
        verify(mailSender).send(any(MimeMessage.class));
    }
}
