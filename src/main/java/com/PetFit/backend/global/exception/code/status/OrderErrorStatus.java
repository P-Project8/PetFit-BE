package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorStatus implements BaseCodeInterface {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER001", "주문을 찾을 수 없습니다."),
    EMPTY_CART(HttpStatus.BAD_REQUEST, "ORDER002", "장바구니가 비어있습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "ORDER003", "상품 재고가 부족합니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "ORDER004", "이미 취소된 주문입니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "ORDER005", "취소할 수 없는 주문 상태입니다.");

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
