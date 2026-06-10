package com.PetFit.backend.subscription.domain.repository;

import com.PetFit.backend.subscription.domain.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findFirstByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);

    @Query("SELECT s FROM Subscription s " +
            "WHERE s.status = 'ACTIVE' AND s.plan = 'PREMIUM' " +
            "AND s.endDate IS NOT NULL AND s.endDate < :now " +
            "AND s.deletedAt IS NULL")
    List<Subscription> findExpiredPremiums(@Param("now") LocalDateTime now);

    // ===== Admin Stats =====

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.deletedAt IS NULL AND s.status = 'ACTIVE' AND s.plan = :plan")
    long countActiveByPlan(@Param("plan") String plan);
}
