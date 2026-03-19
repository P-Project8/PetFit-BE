package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WishlistErrorStatus implements BaseCodeInterface {

    WISHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "WISH001", "찜 항목을 찾을 수 없습니다."),
    ALREADY_WISHLISTED(HttpStatus.BAD_REQUEST, "WISH002", "이미 찜한 상품입니다.");

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
