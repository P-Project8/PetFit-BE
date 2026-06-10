package com.PetFit.backend.gallery.domain.repository;

import com.PetFit.backend.gallery.domain.entity.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    Optional<Gallery> findByIdAndDeletedAtIsNull(Long id);

    Page<Gallery> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    Page<Gallery> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * 인기 게시물: 좋아요 수 + 최근성 가중치.
     * 점수 = likeCount * 2 + commentCount + (최근 7일 이내면 + 5)
     */
    @Query("SELECT g FROM Gallery g " +
            "WHERE g.deletedAt IS NULL " +
            "ORDER BY (g.likeCount * 2 + g.commentCount + " +
            "CASE WHEN g.createdAt >= :recentThreshold THEN 5 ELSE 0 END) DESC, " +
            "g.createdAt DESC")
    Page<Gallery> findPopular(LocalDateTime recentThreshold, Pageable pageable);

    // ===== Admin Stats =====

    @Query("SELECT COUNT(g) FROM Gallery g WHERE g.deletedAt IS NULL")
    long countAll();

    @Query("SELECT COALESCE(SUM(g.likeCount), 0) FROM Gallery g WHERE g.deletedAt IS NULL")
    long sumLikes();

    @Query("SELECT COALESCE(SUM(g.commentCount), 0) FROM Gallery g WHERE g.deletedAt IS NULL")
    long sumComments();
}
