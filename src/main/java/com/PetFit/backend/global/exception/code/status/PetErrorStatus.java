package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PetErrorStatus implements BaseCodeInterface {

    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET001", "반려견 프로필을 찾을 수 없습니다."),
    PET_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PET002", "본인의 반려견 프로필만 접근할 수 있습니다."),
    PET_INVALID_DIMENSION(HttpStatus.BAD_REQUEST, "PET003", "체형 정보(체중/둘레/길이)는 0보다 커야 합니다."),
    PET_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PET004", "반려견 프로필은 최대 5마리까지 등록할 수 있습니다."),
    PET_SIZE_RECOMMENDATION_UNAVAILABLE(HttpStatus.BAD_REQUEST, "PET005", "사이즈를 추천할 수 있는 옵션이 없습니다.");

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
