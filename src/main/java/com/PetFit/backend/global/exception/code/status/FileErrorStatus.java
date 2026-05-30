package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileErrorStatus implements BaseCodeInterface {

    FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE001", "업로드된 파일이 비어있습니다."),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "FILE002", "지원하지 않는 파일 형식입니다. (이미지만 업로드 가능)"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE003", "파일 크기가 10MB를 초과합니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE004", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE005", "파일 삭제에 실패했습니다."),
    FILE_INVALID_URL(HttpStatus.BAD_REQUEST, "FILE006", "유효하지 않은 파일 URL입니다."),
    FILE_S3_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE007", "S3가 설정되지 않았습니다.");

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
