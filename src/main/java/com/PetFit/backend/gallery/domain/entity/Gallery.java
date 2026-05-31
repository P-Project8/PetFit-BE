package com.PetFit.backend.gallery.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Pet-Gallery 게시물.
 * AI 스타일링 결과를 사용자가 직접 갤러리에 공유한다.
 */
@Entity
@Getter
@Table(name = "gallery_posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gallery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column
    private Long petProfileId;     // 어떤 반려견 (옵션)

    @Column
    private Long productId;        // 어떤 상품을 가상 피팅 했는지 (옵션 - 상품 태그)

    @Column
    private Long stylingId;        // AiStyling 이력 연결 (옵션)

    @Column(nullable = false, length = 1000)
    private String imageUrl;       // AI 결과 이미지 URL

    @Column(length = 500)
    private String caption;        // 사용자 설명

    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer commentCount = 0;

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void incrementComment() {
        this.commentCount++;
    }

    public void decrementComment() {
        if (this.commentCount > 0) this.commentCount--;
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
}
