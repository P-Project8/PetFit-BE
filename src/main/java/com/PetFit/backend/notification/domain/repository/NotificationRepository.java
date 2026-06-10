package com.PetFit.backend.notification.domain.repository;

import com.PetFit.backend.notification.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            String userId, Pageable pageable);

    long countByUserIdAndIsReadFalseAndDeletedAtIsNull(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.userId = :userId AND n.isRead = false AND n.deletedAt IS NULL")
    int markAllReadByUserId(@Param("userId") String userId);
}
