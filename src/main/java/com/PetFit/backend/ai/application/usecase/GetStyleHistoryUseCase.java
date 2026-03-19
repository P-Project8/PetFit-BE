package com.PetFit.backend.ai.application.usecase;

import com.PetFit.backend.ai.domain.service.AiStylingService;
import com.PetFit.backend.ai.presentation.dto.response.StyleHistoryResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStyleHistoryUseCase {

    private final AiStylingService aiStylingService;

    public List<StyleHistoryResponse> execute(String userId) {
        return aiStylingService.findAllByUserId(userId).stream()
                .map(StyleHistoryResponse::from)
                .toList();
    }
}
