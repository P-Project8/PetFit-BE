package com.PetFit.backend.global.swagger;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * 공통 보안 설정을 위한 기본 API 인터페이스
 */
@SecurityRequirement(name = "Bearer Authentication")
public interface BaseApi {
}
