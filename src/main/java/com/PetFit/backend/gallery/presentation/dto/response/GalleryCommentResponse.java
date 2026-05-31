package com.PetFit.backend.gallery.presentation.dto.response;

import com.PetFit.backend.gallery.domain.entity.GalleryComment;

import java.time.LocalDateTime;

public record GalleryCommentResponse(
        Long id,
        Long galleryId,
        String userId,
        String content,
        LocalDateTime createdAt
) {
    public static GalleryCommentResponse from(GalleryComment c) {
        return new GalleryCommentResponse(
                c.getId(), c.getGalleryId(), c.getUserId(), c.getContent(), c.getCreatedAt()
        );
    }
}
