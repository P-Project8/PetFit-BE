package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiErrorStatus implements BaseCodeInterface {

    AI_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI001", "AI 서비스 호출에 실패했습니다."),
    AI_EMPTY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AI002", "AI 응답이 비어있습니다."),
    AI_NO_IMAGE_GENERATED(HttpStatus.INTERNAL_SERVER_ERROR, "AI003", "AI가 이미지를 생성하지 못했습니다."),
    AI_RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI004", "AI 응답 파싱에 실패했습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI005", "이미지 업로드에 실패했습니다."),
    AI_INVALID_INPUT(HttpStatus.BAD_REQUEST, "AI006", "강아지나 옷 사진이 아닌 것 같습니다. 다시 확인해주세요.");

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
