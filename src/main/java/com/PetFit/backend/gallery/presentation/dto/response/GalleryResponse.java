package com.PetFit.backend.gallery.presentation.dto.response;

import com.PetFit.backend.gallery.domain.entity.Gallery;

import java.time.LocalDateTime;

public record GalleryResponse(
        Long id,
        String userId,
        Long petProfileId,
        Long productId,
        Long stylingId,
        String imageUrl,
        String caption,
        Integer likeCount,
        Integer commentCount,
        Boolean liked,
        LocalDateTime createdAt
) {
    public static GalleryResponse from(Gallery g, boolean liked) {
        return new GalleryResponse(
                g.getId(), g.getUserId(),
                g.getPetProfileId(), g.getProductId(), g.getStylingId(),
                g.getImageUrl(), g.getCaption(),
                g.getLikeCount(), g.getCommentCount(),
                liked,
                g.getCreatedAt()
        );
    }
}
