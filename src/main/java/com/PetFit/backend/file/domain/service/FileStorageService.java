package com.PetFit.backend.file.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.FileErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 공통 파일 저장 서비스 (S3 기반).
 * <p>
 * 각 도메인별로 폴더를 구분하여 저장한다.
 * 예: pets/, gallery/, reviews/, styling/
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * MultipartFile을 지정된 폴더에 업로드 후 공개 URL 반환.
     *
     * @param file   업로드할 파일
     * @param folder 저장 폴더 (예: "pets", "gallery", "reviews", "styling")
     * @param userId 사용자 ID (사용자별 격리)
     * @return S3 공개 URL
     */
    public String upload(MultipartFile file, String folder, String userId) {
        validateFile(file);

        String key = buildKey(folder, userId, file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String url = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucket).key(key))
                    .toExternalForm();

            log.info("파일 업로드 성공: {}", key);
            return url;
        } catch (IOException e) {
            log.error("파일 업로드 실패 (IOException): {}", e.getMessage());
            throw new RestApiException(FileErrorStatus.FILE_UPLOAD_FAILED);
        } catch (S3Exception e) {
            log.error("S3 업로드 실패: {}", e.getMessage());
            throw new RestApiException(FileErrorStatus.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Base64 인코딩된 이미지를 업로드.
     *
     * @param base64Data  Base64 데이터 (data: URI prefix 가능)
     * @param folder      저장 폴더
     * @param userId      사용자 ID
     * @param contentType MIME 타입 (예: "image/png")
     * @return S3 공개 URL
     */
    public String uploadBase64(String base64Data, String folder, String userId, String contentType) {
        if (base64Data == null || base64Data.isBlank()) {
            throw new RestApiException(FileErrorStatus.FILE_EMPTY);
        }

        // data: URI prefix 제거
        String cleanBase64 = base64Data.contains(",")
                ? base64Data.substring(base64Data.indexOf(",") + 1)
                : base64Data;

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(cleanBase64);
        } catch (IllegalArgumentException e) {
            log.error("Base64 디코딩 실패: {}", e.getMessage());
            throw new RestApiException(FileErrorStatus.FILE_INVALID_TYPE);
        }

        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new RestApiException(FileErrorStatus.FILE_TOO_LARGE);
        }

        String extension = extractExtensionFromContentType(contentType);
        String key = buildKey(folder, userId, "result." + extension);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType != null ? contentType : "image/png")
                    .contentLength((long) imageBytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            String url = s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucket).key(key))
                    .toExternalForm();

            log.info("Base64 업로드 성공: {}", key);
            return url;
        } catch (S3Exception e) {
            log.error("S3 Base64 업로드 실패: {}", e.getMessage());
            throw new RestApiException(FileErrorStatus.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 파일 URL로부터 S3 객체 삭제.
     *
     * @param fileUrl S3 공개 URL
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new RestApiException(FileErrorStatus.FILE_INVALID_URL);
        }

        String key = extractKeyFromUrl(fileUrl);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("파일 삭제 성공: {}", key);
        } catch (S3Exception e) {
            log.error("S3 삭제 실패: {}", e.getMessage());
            throw new RestApiException(FileErrorStatus.FILE_DELETE_FAILED);
        }
    }

    // ===== Private helpers =====

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RestApiException(FileErrorStatus.FILE_EMPTY);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RestApiException(FileErrorStatus.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new RestApiException(FileErrorStatus.FILE_INVALID_TYPE);
        }
    }

    private String buildKey(String folder, String userId, String originalFilename) {
        String extension = extractExtension(originalFilename);
        String uniqueName = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%s/%s.%s", folder, userId, uniqueName, extension);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String extractExtensionFromContentType(String contentType) {
        if (contentType == null) return "png";
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> "png";
        };
    }

    private String extractKeyFromUrl(String url) {
        // https://bucket.s3.region.amazonaws.com/folder/userId/file.jpg
        // -> folder/userId/file.jpg
        int idx = url.indexOf(".amazonaws.com/");
        if (idx < 0) {
            throw new RestApiException(FileErrorStatus.FILE_INVALID_URL);
        }
        return url.substring(idx + ".amazonaws.com/".length());
    }
}
