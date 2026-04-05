package com.PetFit.backend.ai.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AiErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(software.amazon.awssdk.services.s3.S3Client.class)
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucket).key(uniqueFileName))
                    .toExternalForm();
        } catch (IOException e) {
            log.error("S3 업로드 실패: {}", e.getMessage());
            throw new RestApiException(AiErrorStatus.S3_UPLOAD_FAILED);
        }
    }

    public String uploadBase64Image(String base64Data, String fileName) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueFileName)
                .contentType("image/png")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucket).key(uniqueFileName))
                .toExternalForm();
    }

    public String convertToBase64(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            log.error("Base64 변환 실패: {}", e.getMessage());
            throw new RestApiException(AiErrorStatus.S3_UPLOAD_FAILED);
        }
    }
}
