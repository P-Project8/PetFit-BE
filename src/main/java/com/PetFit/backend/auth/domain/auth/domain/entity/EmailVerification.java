package com.PetFit.backend.auth.domain.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "email_verifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String code;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer attemptCount = 0;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public void updateCode(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.verified = false;
        this.attemptCount = 0;
        this.expiresAt = expiresAt;
    }

    public void markVerified() {
        this.verified = true;
    }

    public void incrementAttempt() {
        this.attemptCount++;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isMaxAttemptsExceeded(int maxAttempts) {
        return attemptCount >= maxAttempts;
    }
}
