package com.nexo.manada_solidaria_backend.images.services.interfaces;

import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import com.nexo.manada_solidaria_backend.images.controllers.responses.PresignedUrlResponse;

public interface ImageService {
    PresignedUrlResponse getPresignedUrl(CreatePresignedUrlRequest createPresignedUrlRequest);
}
