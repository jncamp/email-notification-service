package com.example.notification.repository;

import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {

    List<NotificationRequest> findTop10ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
            NotificationStatus status,
            Integer maxRetries
    );

    List<NotificationRequest> findTop10ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            List<NotificationStatus> statuses,
            Integer maxRetries
    );
}
