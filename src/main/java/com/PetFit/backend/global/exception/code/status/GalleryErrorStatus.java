package com.PetFit.backend.global.exception.code.status;

import org.springframework.http.HttpStatus;

import com.PetFit.backend.global.exception.code.BaseCode;
import com.PetFit.backend.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GalleryErrorStatus implements BaseCodeInterface {

    GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "GAL001", "갤러리 게시물을 찾을 수 없습니다."),
    GALLERY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "GAL002", "본인 게시물만 수정/삭제할 수 있습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "GAL003", "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "GAL004", "본인 댓글만 수정/삭제할 수 있습니다.");

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
