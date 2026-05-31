package com.PetFit.backend.gallery.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "gallery_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"galleryId", "userId"})
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long galleryId;

    @Column(nullable = false)
    private String userId;
}
