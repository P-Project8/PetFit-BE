package com.PetFit.backend.ai.domain.repository;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AiStylingRepository extends JpaRepository<AiStyling, Long> {

    List<AiStyling> findAllByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * 특정 기간 내 사용자의 스타일링 시도 횟수.
     * FAILED 상태도 차감 대상으로 포함 (외부 API 호출 자체가 비용이므로).
     * 실패만 제외하려면 status <> 'FAILED' 조건 추가.
     */
    @Query("SELECT COUNT(a) FROM AiStyling a " +
            "WHERE a.userId = :userId " +
            "AND a.createdAt >= :start AND a.createdAt < :end " +
            "AND a.deletedAt IS NULL " +
            "AND a.status <> 'FAILED'")
    long countByUserIdInPeriod(@Param("userId") String userId,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);
}
