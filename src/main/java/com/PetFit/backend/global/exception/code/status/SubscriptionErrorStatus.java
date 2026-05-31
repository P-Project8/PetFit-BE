package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionErrorStatus implements BaseCodeInterface {

    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "SUB001", "구독 정보를 찾을 수 없습니다."),
    ALREADY_PREMIUM(HttpStatus.BAD_REQUEST, "SUB002", "이미 프리미엄 플랜을 구독 중입니다."),
    CANNOT_CANCEL_FREE(HttpStatus.BAD_REQUEST, "SUB003", "FREE 플랜은 취소할 수 없습니다."),
    CREDIT_LIMIT_EXCEEDED(HttpStatus.PAYMENT_REQUIRED, "SUB004", "이번 달 AI 스타일링 크레딧을 모두 사용했습니다. 프리미엄으로 업그레이드해 주세요.");

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
    private final String code;
    private final String message;

    @Override
    public BaseCode getCode() {
        return BaseCode.builder()
                .httpStatus(httpStatus)
                .isSuccess(isSuccess)
                .code(code)
                .message(message)
                .build();
    }
}
