package com.PetFit.backend.pet.domain.repository;

import com.PetFit.backend.pet.domain.entity.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetProfileRepository extends JpaRepository<PetProfile, Long> {

    List<PetProfile> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(String userId);

    Optional<PetProfile> findByIdAndDeletedAtIsNull(Long id);

    long countByUserIdAndDeletedAtIsNull(String userId);

    /**
     * 가슴둘레 범위 안에 있는 다른 사용자의 반려견 프로필을 조회.
     * 본인은 제외하고 중복 사용자 ID를 한 번씩만 반환.
     */
    @Query("SELECT DISTINCT p.userId FROM PetProfile p " +
            "WHERE p.deletedAt IS NULL " +
            "AND p.userId <> :excludeUserId " +
            "AND p.chestSize BETWEEN :minChest AND :maxChest")
    List<String> findUserIdsWithSimilarChestSize(
            @Param("excludeUserId") String excludeUserId,
            @Param("minChest") Double minChest,
            @Param("maxChest") Double maxChest);
}
