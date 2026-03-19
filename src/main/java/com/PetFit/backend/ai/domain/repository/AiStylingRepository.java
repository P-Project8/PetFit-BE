package com.PetFit.backend.ai.domain.repository;

import com.PetFit.backend.ai.domain.entity.AiStyling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiStylingRepository extends JpaRepository<AiStyling, Long> {

    List<AiStyling> findAllByUserIdOrderByCreatedAtDesc(String userId);
}
