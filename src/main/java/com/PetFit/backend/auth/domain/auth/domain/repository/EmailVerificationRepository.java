package com.PetFit.backend.auth.domain.auth.domain.repository;

import com.PetFit.backend.auth.domain.auth.domain.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);
}
