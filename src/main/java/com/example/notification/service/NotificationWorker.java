package com.example.notification.service;

import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.repository.NotificationRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class NotificationWorker {

    private static final int MAX_RETRIES = 3;

    // ⬇️ backoff delay (adjust if you want)
    private static final long RETRY_DELAY_SECONDS = 60;

    private final NotificationRequestRepository repository;
    private final EmailSenderService emailSenderService;

    public NotificationWorker(NotificationRequestRepository repository,
                              EmailSenderService emailSenderService) {
        this.repository = repository;
        this.emailSenderService = emailSenderService;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processPendingNotifications() {
        List<NotificationRequest> workList =
                repository.findTop10ByStatusInOrderByCreatedAtAsc(
                        List.of(NotificationStatus.PENDING, NotificationStatus.FAILED));

        System.out.println("DEBUG worker fired at " + OffsetDateTime.now());
        System.out.println("DEBUG workList size = " + workList.size());

        for (NotificationRequest n : workList) {

            // 🚫 stop if max retries reached
            if (n.getStatus() == NotificationStatus.FAILED &&
                    n.getRetryCount() >= MAX_RETRIES) {
                System.out.println("DEBUG skipping id=" + n.getId() + " because max retries reached");
                continue;
            }

            // ⏳ BACKOFF LOGIC (this is the key addition)
            if (n.getStatus() == NotificationStatus.FAILED &&
                    n.getUpdatedAt() != null) {

                Duration sinceLastAttempt = Duration.between(
                        n.getUpdatedAt(),
                        OffsetDateTime.now()
                );

                if (sinceLastAttempt.getSeconds() < RETRY_DELAY_SECONDS) {
                    System.out.println("DEBUG skipping id=" + n.getId() +
                            " due to backoff (" + sinceLastAttempt.getSeconds() + "s elapsed)");
                    continue;
                }
            }

            try {
                System.out.println("DEBUG picked notification id=" + n.getId());

                n.setStatus(NotificationStatus.PROCESSING);
                n.setUpdatedAt(OffsetDateTime.now());
                n.setLastError(null);
                repository.saveAndFlush(n);

                System.out.println("DEBUG about to call emailSenderService.send for id=" + n.getId());

                emailSenderService.send(n);

                System.out.println("DEBUG emailSenderService.send returned for id=" + n.getId());

                n.setStatus(NotificationStatus.SENT);
                n.setUpdatedAt(OffsetDateTime.now());
                repository.saveAndFlush(n);

                System.out.println("DEBUG marked SENT for id=" + n.getId());

            } catch (Exception ex) {
                int retries = n.getRetryCount() == null ? 0 : n.getRetryCount();
                n.setRetryCount(retries + 1);
                n.setStatus(NotificationStatus.FAILED);
                n.setLastError(ex.getClass().getName() + ": " + ex.getMessage());
                n.setUpdatedAt(OffsetDateTime.now());
                repository.saveAndFlush(n);

                System.out.println("DEBUG marked FAILED for id=" + n.getId());
                ex.printStackTrace();
            }
        }
    }
}