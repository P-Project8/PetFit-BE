package com.PetFit.backend.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.PetFit.backend.auth.domain.auth.domain.service.RefreshTokenService;
import com.PetFit.backend.auth.domain.auth.domain.service.TokenWhitelistService;
import com.PetFit.backend.global.config.properties.CorsProperties;
import com.PetFit.backend.global.security.ExcludeAuthPathProperties;
import com.PetFit.backend.global.security.JwtAuthenticationFilter;
import com.PetFit.backend.global.security.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final ExcludeAuthPathProperties excludeAuthPathProperties;
    private final RefreshTokenService refreshTokenService;
    private final TokenWhitelistService tokenWhitelistService;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request -> {
            excludeAuthPathProperties.getPaths().iterator()
                    .forEachRemaining(authPath ->
                            request.requestMatchers(HttpMethod.valueOf(authPath.getMethod()), authPath.getPathPattern()).permitAll()
                    );
        });

        http.authorizeHttpRequests(request -> request
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-resources/**"
                ).permitAll()
                .requestMatchers(
                        "/actuator/**",
                        "/favicon.ico"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/users/token").authenticated() // 토큰 재발급
                // Authenticated
                .anyRequest().authenticated()
        );

        // Session 해제
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Jwt 커스텀 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Token Exception Handling
        http.exceptionHandling(except -> except
                .authenticationEntryPoint((request, response, authException) -> response.sendError(response.getStatus(), "토큰 오류"))
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 HTTP 메서드
        config.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        // 허용할 헤더
        config.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        
        // 모든 origin 허용인 경우 특별 처리
        if ("*".equals(corsProperties.getAllowedOrigins())) {
            config.addAllowedOriginPattern("*");
            config.setAllowCredentials(false);  // "*"와 함께 사용할 수 없음
        } else {
            config.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
            config.setAllowCredentials(true);
        }
        
        // pre-flight 캐시 시간 (초)
        config.setMaxAge(corsProperties.getMaxAge());
        
        // Swagger UI를 위한 추가 설정
        config.addExposedHeader("X-User-Id");
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위 정책을 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, excludeAuthPathProperties, refreshTokenService, tokenWhitelistService);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

