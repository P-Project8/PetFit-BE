package com.PetFit.backend.gallery.domain.service;

import com.PetFit.backend.gallery.domain.entity.Gallery;
import com.PetFit.backend.gallery.domain.entity.GalleryComment;
import com.PetFit.backend.gallery.domain.entity.GalleryLike;
import com.PetFit.backend.gallery.domain.repository.GalleryCommentRepository;
import com.PetFit.backend.gallery.domain.repository.GalleryLikeRepository;
import com.PetFit.backend.gallery.domain.repository.GalleryRepository;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.GalleryErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryCommentRepository commentRepository;
    private final GalleryLikeRepository likeRepository;

    // ===== Gallery =====

    public Gallery findByIdOrThrow(Long id) {
        return galleryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RestApiException(GalleryErrorStatus.GALLERY_NOT_FOUND));
    }

    public Gallery findOwnedOrThrow(Long id, String userId) {
        Gallery g = findByIdOrThrow(id);
        if (!g.isOwnedBy(userId)) {
            throw new RestApiException(GalleryErrorStatus.GALLERY_ACCESS_DENIED);
        }
        return g;
    }

    public Gallery save(Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    public void softDelete(Gallery gallery) {
        gallery.deleted();
        galleryRepository.save(gallery);
    }

    public Page<Gallery> findFeed(Pageable pageable) {
        return galleryRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }

    public Page<Gallery> findMyGalleries(String userId, Pageable pageable) {
        return galleryRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Gallery> findPopular(Pageable pageable) {
        return galleryRepository.findPopular(LocalDateTime.now().minusDays(7), pageable);
    }

    // ===== Comment =====

    public GalleryComment findCommentOrThrow(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new RestApiException(GalleryErrorStatus.COMMENT_NOT_FOUND));
    }

    public GalleryComment saveComment(GalleryComment comment) {
        return commentRepository.save(comment);
    }

    public void softDeleteComment(GalleryComment comment) {
        comment.deleted();
        commentRepository.save(comment);
    }

    public Page<GalleryComment> findComments(Long galleryId, Pageable pageable) {
        return commentRepository.findAllByGalleryIdAndDeletedAtIsNullOrderByCreatedAtAsc(galleryId, pageable);
    }

    // ===== Like =====

    public boolean isLiked(Long galleryId, String userId) {
        return likeRepository.existsByGalleryIdAndUserId(galleryId, userId);
    }

    /**
     * 좋아요 토글. true=좋아요 추가, false=취소.
     */
    public boolean toggleLike(Long galleryId, String userId) {
        return likeRepository.findByGalleryIdAndUserId(galleryId, userId)
                .map(existing -> {
                    likeRepository.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    likeRepository.save(GalleryLike.builder()
                            .galleryId(galleryId).userId(userId).build());
                    return true;
                });
    }

    public Set<Long> findLikedGalleryIds(Collection<Long> galleryIds, String userId) {
        if (galleryIds.isEmpty()) return Set.of();
        return likeRepository.findAllByGalleryIdInAndUserId(galleryIds, userId).stream()
                .map(GalleryLike::getGalleryId)
                .collect(Collectors.toSet());
    }
}
