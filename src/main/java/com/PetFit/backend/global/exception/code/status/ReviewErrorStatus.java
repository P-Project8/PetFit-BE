package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorStatus implements BaseCodeInterface {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW001", "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_ELIGIBLE(HttpStatus.BAD_REQUEST, "REVIEW002", "해당 상품을 주문한 이력이 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REVIEW003", "이미 리뷰를 작성하였습니다.");

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
