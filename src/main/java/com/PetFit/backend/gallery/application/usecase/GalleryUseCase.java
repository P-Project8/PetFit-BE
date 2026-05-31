package com.PetFit.backend.gallery.application.usecase;

import com.PetFit.backend.gallery.domain.entity.Gallery;
import com.PetFit.backend.gallery.domain.entity.GalleryComment;
import com.PetFit.backend.gallery.domain.service.GalleryService;
import com.PetFit.backend.gallery.presentation.dto.request.CreateCommentRequest;
import com.PetFit.backend.gallery.presentation.dto.request.CreateGalleryRequest;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryCommentResponse;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryResponse;
import com.PetFit.backend.gallery.presentation.dto.response.LikeToggleResponse;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.GalleryErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class GalleryUseCase {

    private final GalleryService galleryService;

    // ===== Gallery =====

    public GalleryResponse create(String userId, CreateGalleryRequest request) {
        Gallery g = Gallery.builder()
                .userId(userId)
                .petProfileId(request.petProfileId())
                .productId(request.productId())
                .stylingId(request.stylingId())
                .imageUrl(request.imageUrl())
                .caption(request.caption())
                .build();
        return GalleryResponse.from(galleryService.save(g), false);
    }

    public void delete(String userId, Long galleryId) {
        Gallery g = galleryService.findOwnedOrThrow(galleryId, userId);
        galleryService.softDelete(g);
    }

    @Transactional(readOnly = true)
    public Page<GalleryResponse> findFeed(String currentUserId, Pageable pageable) {
        return enrich(galleryService.findFeed(pageable), currentUserId);
    }

    @Transactional(readOnly = true)
    public Page<GalleryResponse> findPopular(String currentUserId, Pageable pageable) {
        return enrich(galleryService.findPopular(pageable), currentUserId);
    }

    @Transactional(readOnly = true)
    public Page<GalleryResponse> findMine(String userId, Pageable pageable) {
        return enrich(galleryService.findMyGalleries(userId, pageable), userId);
    }

    @Transactional(readOnly = true)
    public GalleryResponse findOne(String currentUserId, Long galleryId) {
        Gallery g = galleryService.findByIdOrThrow(galleryId);
        boolean liked = currentUserId != null && galleryService.isLiked(galleryId, currentUserId);
        return GalleryResponse.from(g, liked);
    }

    // ===== Like =====

    public LikeToggleResponse toggleLike(String userId, Long galleryId) {
        Gallery g = galleryService.findByIdOrThrow(galleryId);
        boolean nowLiked = galleryService.toggleLike(galleryId, userId);
        if (nowLiked) {
            g.incrementLike();
        } else {
            g.decrementLike();
        }
        galleryService.save(g);
        return new LikeToggleResponse(galleryId, nowLiked, g.getLikeCount());
    }

    // ===== Comment =====

    public GalleryCommentResponse createComment(String userId, Long galleryId, CreateCommentRequest request) {
        Gallery g = galleryService.findByIdOrThrow(galleryId);

        GalleryComment comment = GalleryComment.builder()
                .galleryId(galleryId)
                .userId(userId)
                .content(request.content())
                .build();
        GalleryComment saved = galleryService.saveComment(comment);

        g.incrementComment();
        galleryService.save(g);

        return GalleryCommentResponse.from(saved);
    }

    public void deleteComment(String userId, Long commentId) {
        GalleryComment comment = galleryService.findCommentOrThrow(commentId);
        if (!comment.isOwnedBy(userId)) {
            throw new RestApiException(GalleryErrorStatus.COMMENT_ACCESS_DENIED);
        }
        galleryService.softDeleteComment(comment);

        Gallery g = galleryService.findByIdOrThrow(comment.getGalleryId());
        g.decrementComment();
        galleryService.save(g);
    }

    @Transactional(readOnly = true)
    public Page<GalleryCommentResponse> findComments(Long galleryId, Pageable pageable) {
        galleryService.findByIdOrThrow(galleryId); // 존재 확인
        return galleryService.findComments(galleryId, pageable)
                .map(GalleryCommentResponse::from);
    }

    // ===== Helpers =====

    private Page<GalleryResponse> enrich(Page<Gallery> page, String currentUserId) {
        if (currentUserId == null) {
            return page.map(g -> GalleryResponse.from(g, false));
        }
        List<Long> ids = page.getContent().stream().map(Gallery::getId).toList();
        Set<Long> likedSet = galleryService.findLikedGalleryIds(ids, currentUserId);
        return page.map(g -> GalleryResponse.from(g, likedSet.contains(g.getId())));
    }
}
