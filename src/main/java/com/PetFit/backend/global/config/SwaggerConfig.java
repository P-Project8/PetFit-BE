package com.PetFit.backend.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String schemeName = "Bearer Authentication";

        // 1) SecurityScheme 정의
        Components components = new Components()
                .addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        // 2) 전역 SecurityRequirement 추가
        SecurityRequirement requirement = new SecurityRequirement()
                .addList(schemeName);

        // 3) 서버 URL 설정
        Server server = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        return new OpenAPI()
                .servers(List.of(server))
                .components(components)
                .addSecurityItem(requirement)
                .info(new Info()
                        .title("PetFit API")
                        .description("PetFit 백엔드 API")
                        .version("1.0.0")
                );
    }
}