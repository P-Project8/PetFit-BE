package com.PetFit.backend.pet.domain.repository;

import com.PetFit.backend.pet.domain.entity.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetProfileRepository extends JpaRepository<PetProfile, Long> {

    List<PetProfile> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(String userId);

    Optional<PetProfile> findByIdAndDeletedAtIsNull(Long id);

    long countByUserIdAndDeletedAtIsNull(String userId);
}
