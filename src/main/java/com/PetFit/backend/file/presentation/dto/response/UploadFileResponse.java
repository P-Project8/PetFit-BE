package com.PetFit.backend.file.presentation.dto.response;

public record UploadFileResponse(
        String fileUrl,
        String folder,
        Long fileSize,
        String contentType
) {
    public static UploadFileResponse of(String fileUrl, String folder, Long fileSize, String contentType) {
        return new UploadFileResponse(fileUrl, folder, fileSize, contentType);
    }
}
