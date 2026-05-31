package com.PetFit.backend.gallery.domain.repository;

import com.PetFit.backend.gallery.domain.entity.GalleryLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GalleryLikeRepository extends JpaRepository<GalleryLike, Long> {

    Optional<GalleryLike> findByGalleryIdAndUserId(Long galleryId, String userId);

    boolean existsByGalleryIdAndUserId(Long galleryId, String userId);

    List<GalleryLike> findAllByGalleryIdInAndUserId(Collection<Long> galleryIds, String userId);
}
