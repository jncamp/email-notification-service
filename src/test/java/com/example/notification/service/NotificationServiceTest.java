package com.example.notification.service;

import com.example.notification.dto.CreateEmailNotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRequestRepository repository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationRequestRepository.class);
        notificationService = new NotificationService(repository);
    }

    @Test
    void createsPendingNotificationWithZeroRetries() {
        CreateEmailNotificationRequest request = new CreateEmailNotificationRequest();
        request.setTo("j_n_camp@hotmail.com");
        request.setSubject("Test email");
        request.setTemplateId("welcome-email");
        request.setTemplateData("{\"firstName\":\"John\"}");

        when(repository.save(any(NotificationRequest.class))).thenAnswer(invocation -> {
            NotificationRequest saved = invocation.getArgument(0);
            setId(saved, 19L);
            return saved;
        });

        NotificationResponse response = notificationService.createEmailNotification(request);

        assertEquals(19L, response.getId());
        assertEquals("j_n_camp@hotmail.com", response.getTo());
        assertEquals("Test email", response.getSubject());
        assertEquals("welcome-email", response.getTemplateId());
        assertEquals(NotificationStatus.PENDING, response.getStatus());
        assertEquals(0, response.getRetryCount());
        assertNull(response.getLastError());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void returnsNotificationById() {
        NotificationRequest notification = new NotificationRequest();
        setId(notification, 25L);
        notification.setRecipientEmail("j_n_camp@hotmail.com");
        notification.setSubject("Sent email");
        notification.setTemplateId("welcome-email");
        notification.setStatus(NotificationStatus.SENT);
        notification.setRetryCount(0);
        notification.setLastError(null);
        notification.setCreatedAt(OffsetDateTime.now());
        notification.setUpdatedAt(OffsetDateTime.now());

        when(repository.findById(25L)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotification(25L);

        assertEquals(25L, response.getId());
        assertEquals("j_n_camp@hotmail.com", response.getTo());
        assertEquals(NotificationStatus.SENT, response.getStatus());
    }

    @Test
    void throwsWhenNotificationIsMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.getNotification(99L));

        assertTrue(ex.getMessage().contains("Not found: 99"));
    }

    private void setId(NotificationRequest notification, Long id) {
        try {
            var field = NotificationRequest.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(notification, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
