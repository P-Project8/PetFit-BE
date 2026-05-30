package com.PetFit.backend.file.application.usecase;

import com.PetFit.backend.file.domain.service.FileStorageService;
import com.PetFit.backend.file.presentation.dto.response.UploadFileResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadFileUseCase {

    private final FileStorageService fileStorageService;

    public UploadFileResponse upload(MultipartFile file, String folder, String userId) {
        String url = fileStorageService.upload(file, folder, userId);
        return UploadFileResponse.of(url, folder, file.getSize(), file.getContentType());
    }

    public void delete(String fileUrl) {
        fileStorageService.delete(fileUrl);
    }
}
