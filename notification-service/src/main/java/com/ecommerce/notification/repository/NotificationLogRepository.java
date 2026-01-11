package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByUserId(Long userId);
    List<NotificationLog> findByType(String type);
    List<NotificationLog> findByStatus(NotificationLog.Status status);
}