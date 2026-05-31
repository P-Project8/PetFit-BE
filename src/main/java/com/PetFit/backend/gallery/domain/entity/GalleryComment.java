package com.PetFit.backend.gallery.domain.entity;

import com.PetFit.backend.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "gallery_comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long galleryId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 500)
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
}
