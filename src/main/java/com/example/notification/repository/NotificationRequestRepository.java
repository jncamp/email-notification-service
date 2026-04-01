package com.example.notification.repository;

import com.example.notification.entity.NotificationRequest;
import com.example.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {

    List<NotificationRequest> findTop10ByStatusOrderByCreatedAtAsc(NotificationStatus status);

    List<NotificationRequest> findTop10ByStatusInOrderByCreatedAtAsc(List<NotificationStatus> statuses);
}