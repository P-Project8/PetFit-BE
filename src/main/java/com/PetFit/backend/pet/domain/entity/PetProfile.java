package com.PetFit.backend.pet.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 반려견 상세 프로필.
 * AI 스타일링 시 견종/체형 데이터를 함께 입력하여 사이즈/스타일 추천 정확도를 높인다.
 */
@Entity
@Getter
@Table(name = "pet_profiles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String name;          // 반려견 이름

    @Column(nullable = false, length = 50)
    private String breed;         // 견종 (예: 푸들, 시츄)

    @Column(nullable = false)
    private Integer age;          // 나이 (년)

    @Column(nullable = false)
    private Double weight;        // 체중 (kg)

    @Column(nullable = false)
    private Double neckSize;      // 목 둘레 (cm)

    @Column(nullable = false)
    private Double chestSize;     // 가슴 둘레 (cm)

    @Column(nullable = false)
    private Double backLength;    // 등 길이 (cm)

    @Column(length = 1000)
    private String imageUrl;      // 반려견 사진 (S3 URL)

    public void update(String name, String breed, Integer age,
                       Double weight, Double neckSize, Double chestSize, Double backLength,
                       String imageUrl) {
        if (name != null) this.name = name;
        if (breed != null) this.breed = breed;
        if (age != null) this.age = age;
        if (weight != null) this.weight = weight;
        if (neckSize != null) this.neckSize = neckSize;
        if (chestSize != null) this.chestSize = chestSize;
        if (backLength != null) this.backLength = backLength;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
}
