package com.nexo.manada_solidaria_backend.images.services.implementations;

import com.nexo.manada_solidaria_backend.images.config.R2Properties;
import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import com.nexo.manada_solidaria_backend.images.controllers.responses.PresignedUrlResponse;
import com.nexo.manada_solidaria_backend.images.services.interfaces.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private final S3Presigner r2Presigner;
    private final R2Properties r2Properties;

    @Override
    public PresignedUrlResponse getPresignedUrl(CreatePresignedUrlRequest request) {
        validateFile(request);

        String extension = extensionFor(request.contentType());

        String imageId = "posts/" + UUID.randomUUID() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.getBucket())
                .key(imageId)
                .contentType(request.contentType())
                .build();

        Duration expiration = Duration.ofMinutes(5);

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(expiration)
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                r2Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponse(
                imageId,
                presignedRequest.url().toString(),
                Instant.now().plus(expiration)
        );
    }

    private void validateFile(CreatePresignedUrlRequest request) {
        if (!ALLOWED_CONTENT_TYPES.contains(request.contentType())) {
            throw new IllegalArgumentException(
                    "El formato de imagen no está permitido."
            );
        }

        if (request.fileSize() <= 0 || request.fileSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 5 MB."
            );
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpg", "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Formato no soportado."
            );
        };
    }
}
