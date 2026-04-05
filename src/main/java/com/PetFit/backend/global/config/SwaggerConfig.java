package com.PetFit.backend.global.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "Bearer Authentication";

        Components components = new Components()
                .addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        SecurityRequirement requirement = new SecurityRequirement()
                .addList(schemeName);

        // 상대 경로 사용 - 현재 접속한 호스트로 자동 요청
        Server server = new Server()
                .url("")
                .description("Current Server");

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
