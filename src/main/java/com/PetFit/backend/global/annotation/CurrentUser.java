package com.PetFit.backend.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
    /**
     * false로 설정하면 토큰이 없거나 유효하지 않을 때 401 대신 null을 반환한다.
     * 공개 엔드포인트에서 로그인 사용자 컨텍스트가 옵션일 때 사용.
     */
    boolean required() default true;
}
