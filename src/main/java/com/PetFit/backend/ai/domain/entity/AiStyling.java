package com.PetFit.backend.ai.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ai_stylings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiStyling extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column
    private Long productId;

    @Column(nullable = false, length = 1000)
    private String petImageUrl;

    @Column(length = 1000)
    private String clothImageUrl;

    @Column(length = 1000)
    private String resultImageUrl;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    public void complete(String resultImageUrl) {
        this.resultImageUrl = resultImageUrl;
        this.status = "COMPLETED";
    }

    public void fail() {
        this.status = "FAILED";
    }
}
