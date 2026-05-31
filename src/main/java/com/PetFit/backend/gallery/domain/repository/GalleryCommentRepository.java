package com.PetFit.backend.gallery.domain.repository;

import com.PetFit.backend.gallery.domain.entity.GalleryComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GalleryCommentRepository extends JpaRepository<GalleryComment, Long> {

    Optional<GalleryComment> findByIdAndDeletedAtIsNull(Long id);

    Page<GalleryComment> findAllByGalleryIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long galleryId, Pageable pageable);
}
