package com.PetFit.backend.file.presentation;

import com.PetFit.backend.file.application.usecase.UploadFileUseCase;
import com.PetFit.backend.file.presentation.dto.response.UploadFileResponse;
import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.FileApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController implements FileApi {

    private final UploadFileUseCase uploadFileUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public BaseResponse<UploadFileResponse> upload(
            @Parameter(hidden = true) @CurrentUser String userId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "etc") String folder) {
        return BaseResponse.onSuccess(uploadFileUseCase.upload(file, folder, userId));
    }

    @DeleteMapping
    @Override
    public BaseResponse<Void> delete(
            @Parameter(hidden = true) @CurrentUser String userId,
            @RequestParam("fileUrl") String fileUrl) {
        uploadFileUseCase.delete(fileUrl);
        return BaseResponse.onSuccess(null);
    }
}
