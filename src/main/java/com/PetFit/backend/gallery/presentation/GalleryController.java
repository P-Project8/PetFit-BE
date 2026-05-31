package com.PetFit.backend.gallery.presentation;

import com.PetFit.backend.gallery.application.usecase.GalleryUseCase;
import com.PetFit.backend.gallery.presentation.dto.request.CreateCommentRequest;
import com.PetFit.backend.gallery.presentation.dto.request.CreateGalleryRequest;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryCommentResponse;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryResponse;
import com.PetFit.backend.gallery.presentation.dto.response.LikeToggleResponse;
import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.GalleryApi;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gallery")
public class GalleryController implements GalleryApi {

    private final GalleryUseCase galleryUseCase;

    @PostMapping
    @Override
    public BaseResponse<GalleryResponse> createGallery(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody CreateGalleryRequest request) {
        return BaseResponse.onSuccess(galleryUseCase.create(userId, request));
    }

    @GetMapping
    @Override
    public BaseResponse<Page<GalleryResponse>> getFeed(
            @Parameter(hidden = true) @CurrentUser(required = false) String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(galleryUseCase.findFeed(userId, pageable));
    }

    @GetMapping("/popular")
    @Override
    public BaseResponse<Page<GalleryResponse>> getPopular(
            @Parameter(hidden = true) @CurrentUser(required = false) String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(galleryUseCase.findPopular(userId, pageable));
    }

    @GetMapping("/my")
    @Override
    public BaseResponse<Page<GalleryResponse>> getMyGalleries(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return BaseResponse.onSuccess(galleryUseCase.findMine(userId, pageable));
    }

    @GetMapping("/{galleryId}")
    @Override
    public BaseResponse<GalleryResponse> getGallery(
            @Parameter(hidden = true) @CurrentUser(required = false) String userId,
            @PathVariable Long galleryId) {
        return BaseResponse.onSuccess(galleryUseCase.findOne(userId, galleryId));
    }

    @DeleteMapping("/{galleryId}")
    @Override
    public BaseResponse<Void> deleteGallery(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long galleryId) {
        galleryUseCase.delete(userId, galleryId);
        return BaseResponse.onSuccess(null);
    }

    @PostMapping("/{galleryId}/like")
    @Override
    public BaseResponse<LikeToggleResponse> toggleLike(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long galleryId) {
        return BaseResponse.onSuccess(galleryUseCase.toggleLike(userId, galleryId));
    }

    @GetMapping("/{galleryId}/comments")
    @Override
    public BaseResponse<Page<GalleryCommentResponse>> getComments(
            @PathVariable Long galleryId,
            @PageableDefault(size = 30) Pageable pageable) {
        return BaseResponse.onSuccess(galleryUseCase.findComments(galleryId, pageable));
    }

    @PostMapping("/{galleryId}/comments")
    @Override
    public BaseResponse<GalleryCommentResponse> createComment(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long galleryId,
            @Valid @RequestBody CreateCommentRequest request) {
        return BaseResponse.onSuccess(galleryUseCase.createComment(userId, galleryId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    @Override
    public BaseResponse<Void> deleteComment(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long commentId) {
        galleryUseCase.deleteComment(userId, commentId);
        return BaseResponse.onSuccess(null);
    }
}
