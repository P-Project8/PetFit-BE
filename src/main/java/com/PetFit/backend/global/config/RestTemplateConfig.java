package com.PetFit.backend.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Gemini 이미지 생성 API는 30~60초 이상 걸릴 수 있어 넉넉히 잡는다.
        factory.setConnectTimeout(10_000);   // 10초
        factory.setReadTimeout(120_000);     // 120초 (2분)

        return builder
                .requestFactory(() -> factory)
                .build();
    }
}
