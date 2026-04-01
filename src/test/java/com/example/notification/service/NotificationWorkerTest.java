package com.example.notification.service;

import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationWorkerTest {

    private NotificationRequestRepository repository;
    private EmailSenderService emailSenderService;
    private NotificationWorker worker;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationRequestRepository.class);
        emailSenderService = mock(EmailSenderService.class);
        worker = new NotificationWorker(repository, emailSenderService);
    }

    @Test
    void marksPendingNotificationSentWhenEmailSendSucceeds() {
        NotificationRequest notification = buildNotification(1L, NotificationStatus.PENDING, 0);
        notification.setSubject("SUCCESS");

        when(repository.findTop10ByStatusInOrderByCreatedAtAsc(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)))
                .thenReturn(List.of(notification));

        when(repository.saveAndFlush(any(NotificationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        worker.processPendingNotifications();

        verify(emailSenderService).send(notification);
        verify(repository, atLeastOnce()).saveAndFlush(any(NotificationRequest.class));
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(0, notification.getRetryCount());
        assertNull(notification.getLastError());
        assertNotNull(notification.getUpdatedAt());
    }

    @Test
    void marksNotificationFailedAndIncrementsRetryCountWhenSendThrows() {
        NotificationRequest notification = buildNotification(2L, NotificationStatus.PENDING, 0);
        notification.setSubject("FAIL_SINGLE");

        when(repository.findTop10ByStatusInOrderByCreatedAtAsc(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)))
                .thenReturn(List.of(notification));

        when(repository.saveAndFlush(any(NotificationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("SMTP failure"))
                .when(emailSenderService).send(any(NotificationRequest.class));

        worker.processPendingNotifications();

        verify(emailSenderService).send(notification);
        verify(repository, atLeastOnce()).saveAndFlush(any(NotificationRequest.class));
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertEquals(1, notification.getRetryCount());
        assertNotNull(notification.getLastError());
        assertTrue(notification.getLastError().contains("SMTP failure"));
        assertNotNull(notification.getUpdatedAt());
    }

    @Test
    void skipsFailedNotificationWhenMaxRetriesReached() {
        NotificationRequest notification = buildNotification(3L, NotificationStatus.FAILED, 3);
        notification.setSubject("MAX_RETRIES");

        when(repository.findTop10ByStatusInOrderByCreatedAtAsc(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)))
                .thenReturn(List.of(notification));

        worker.processPendingNotifications();

        verify(emailSenderService, never()).send(any());
        verify(repository, never()).saveAndFlush(any());
        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertEquals(3, notification.getRetryCount());
    }

    @Test
    void retriesPreviouslyFailedNotificationWhenRetryCountBelowMax() {
        NotificationRequest notification = buildNotification(4L, NotificationStatus.FAILED, 2);
        notification.setSubject("RETRY_OK");

        when(repository.findTop10ByStatusInOrderByCreatedAtAsc(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)))
                .thenReturn(List.of(notification));

        when(repository.saveAndFlush(any(NotificationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        worker.processPendingNotifications();

        verify(emailSenderService).send(notification);
        verify(repository, atLeastOnce()).saveAndFlush(any(NotificationRequest.class));
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(2, notification.getRetryCount());
        assertNull(notification.getLastError());
    }

    @Test
    void processesEachNotificationIndependently() {
        NotificationRequest success = buildNotification(5L, NotificationStatus.PENDING, 0);
        success.setSubject("SUCCESS");

        NotificationRequest failure = buildNotification(6L, NotificationStatus.PENDING, 1);
        failure.setSubject("FAIL");

        when(repository.findTop10ByStatusInOrderByCreatedAtAsc(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED)))
                .thenReturn(List.of(success, failure));

        when(repository.saveAndFlush(any(NotificationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doAnswer(invocation -> {
            NotificationRequest arg = invocation.getArgument(0);
            if ("FAIL".equals(arg.getSubject())) {
                throw new RuntimeException("Mail server down");
            }
            return null;
        }).when(emailSenderService).send(any(NotificationRequest.class));

        worker.processPendingNotifications();

        verify(emailSenderService, times(2)).send(any(NotificationRequest.class));

        assertEquals(NotificationStatus.SENT, success.getStatus());
        assertEquals(NotificationStatus.FAILED, failure.getStatus());
        assertEquals(2, failure.getRetryCount());
        assertNotNull(failure.getLastError());
        assertTrue(failure.getLastError().contains("Mail server down"));
    }

    private NotificationRequest buildNotification(Long id, NotificationStatus status, Integer retryCount) {
        NotificationRequest notification = new NotificationRequest();
        notification.setRecipientEmail("j_n_camp@hotmail.com");
        notification.setSubject("Test email");
        notification.setTemplateId("welcome-email");
        notification.setTemplateData("{\"firstName\":\"John\"}");
        notification.setStatus(status);
        notification.setRetryCount(retryCount);
        notification.setLastError(null);
        notification.setCreatedAt(OffsetDateTime.now().minusMinutes(id));
        notification.setUpdatedAt(OffsetDateTime.now().minusMinutes(id));
        return notification;
    }
}
