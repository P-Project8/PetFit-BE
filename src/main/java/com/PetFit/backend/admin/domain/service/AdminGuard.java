package com.PetFit.backend.admin.domain.service;

import com.PetFit.backend.admin.config.AdminProperties;
import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AdminErrorStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 관리자 권한 가드.
 * 운영 환경에서는 별도 Role 컬럼 + Spring Security 권한 체크로 교체 권장.
 * 현재는 졸업작품 단계의 config 기반 단순 인증.
 */
@Service
@RequiredArgsConstructor
public class AdminGuard {

    private final AdminProperties adminProperties;

    public void verify(String userId) {
        if (!adminProperties.isAdmin(userId)) {
            throw new RestApiException(AdminErrorStatus.ADMIN_ACCESS_DENIED);
        }
    }
}
