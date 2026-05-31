package com.PetFit.backend.gallery.presentation.dto.response;

public record LikeToggleResponse(
        Long galleryId,
        Boolean liked,
        Integer likeCount
) {}
