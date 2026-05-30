package com.PetFit.backend.global.swagger;

import com.PetFit.backend.file.presentation.dto.response.UploadFileResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File", description = "파일 업로드/삭제 API (S3)")
public interface FileApi {

    @Operation(summary = "이미지 업로드",
            description = "S3에 이미지를 업로드합니다. folder 파라미터로 저장 폴더를 지정합니다. " +
                    "지원 폴더: pets, gallery, reviews, styling, etc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 (빈 파일, 지원하지 않는 형식, 크기 초과)"),
            @ApiResponse(responseCode = "500", description = "S3 업로드 실패")
    })
    BaseResponse<UploadFileResponse> upload(
            String userId,
            @Parameter(description = "업로드할 이미지 파일 (jpg, png, webp, gif, 최대 10MB)") MultipartFile file,
            @Parameter(description = "저장 폴더 (예: pets, gallery, reviews)") String folder
    );

    @Operation(summary = "이미지 삭제", description = "S3에서 이미지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 URL"),
            @ApiResponse(responseCode = "500", description = "S3 삭제 실패")
    })
    BaseResponse<Void> delete(
            String userId,
            @Parameter(description = "삭제할 파일의 S3 URL") String fileUrl
    );
}
