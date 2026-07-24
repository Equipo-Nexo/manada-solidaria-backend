package com.nexo.manada_solidaria_backend.images.controllers.requests;

public record CreatePresignedUrlRequest(
        String contentType,
        long fileSize
) {
}
