package com.PetFit.backend.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 관리자 권한 사용자 목록 설정.
 *
 * application.yml:
 * <pre>
 * admin:
 *   user-ids:
 *     - test1234
 *     - admin01
 * </pre>
 */
@ConfigurationProperties(prefix = "admin")
public record AdminProperties(List<String> userIds) {

    public AdminProperties {
        if (userIds == null) {
            userIds = List.of();
        }
    }

    public boolean isAdmin(String userId) {
        return userId != null && userIds.contains(userId);
    }
}
