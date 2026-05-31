package com.PetFit.backend.pet.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.PetErrorStatus;
import com.PetFit.backend.pet.domain.entity.PetProfile;
import com.PetFit.backend.pet.domain.repository.PetProfileRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private static final int MAX_PETS_PER_USER = 5;

    private final PetProfileRepository petProfileRepository;

    public PetProfile findByIdOrThrow(Long id) {
        return petProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RestApiException(PetErrorStatus.PET_NOT_FOUND));
    }

    public PetProfile findOwnedOrThrow(Long id, String userId) {
        PetProfile pet = findByIdOrThrow(id);
        if (!pet.isOwnedBy(userId)) {
            throw new RestApiException(PetErrorStatus.PET_ACCESS_DENIED);
        }
        return pet;
    }

    public List<PetProfile> findAllByUserId(String userId) {
        return petProfileRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(userId);
    }

    public PetProfile save(PetProfile pet) {
        return petProfileRepository.save(pet);
    }

    public void checkLimit(String userId) {
        long count = petProfileRepository.countByUserIdAndDeletedAtIsNull(userId);
        if (count >= MAX_PETS_PER_USER) {
            throw new RestApiException(PetErrorStatus.PET_LIMIT_EXCEEDED);
        }
    }

    public void softDelete(PetProfile pet) {
        pet.deleted();
        petProfileRepository.save(pet);
    }
}
