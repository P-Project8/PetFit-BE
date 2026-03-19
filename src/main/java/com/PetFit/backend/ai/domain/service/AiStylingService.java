package com.PetFit.backend.ai.domain.service;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import com.PetFit.backend.ai.domain.repository.AiStylingRepository;

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
}
