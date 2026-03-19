package com.PetFit.backend.auth.domain.auth.domain.repository;

import com.PetFit.backend.auth.domain.auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(String userId);
    void deleteByUserId(String userId);
}
