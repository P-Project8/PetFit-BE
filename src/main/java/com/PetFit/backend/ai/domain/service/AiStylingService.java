package com.PetFit.backend.ai.domain.service;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import com.PetFit.backend.ai.domain.repository.AiStylingRepository;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AiErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiStylingService {

    private final AiStylingRepository aiStylingRepository;

    public AiStyling save(AiStyling aiStyling) {
        return aiStylingRepository.save(aiStyling);
    }

    public List<AiStyling> findAllByUserId(String userId) {
        return aiStylingRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public AiStyling findByIdOrThrow(Long stylingId) {
        return aiStylingRepository.findById(stylingId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new RestApiException(AiErrorStatus.AI_STYLING_NOT_FOUND));
    }

    public AiStyling findOwnedOrThrow(Long stylingId, String userId) {
        AiStyling styling = findByIdOrThrow(stylingId);
        if (!styling.getUserId().equals(userId)) {
            throw new RestApiException(AiErrorStatus.AI_STYLING_ACCESS_DENIED);
        }
        return styling;
    }
}
