package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductErrorStatus implements BaseCodeInterface {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT001", "상품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT002", "상품 옵션을 찾을 수 없습니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "PRODUCT003", "상품 재고가 부족합니다.");

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
