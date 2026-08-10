package com.nexo.manada_solidaria_backend.images.controllers.implementations;

import com.nexo.manada_solidaria_backend.images.controllers.interfaces.ImageController;
import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import com.nexo.manada_solidaria_backend.images.controllers.responses.PresignedUrlResponse;
import com.nexo.manada_solidaria_backend.images.services.interfaces.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ImageControllerImpl implements ImageController {

    private final ImageService imageService;

    @Override
    public PresignedUrlResponse getPresignedUrl(CreatePresignedUrlRequest createPresignedUrlRequest) {
        return imageService.getPresignedUrl(createPresignedUrlRequest);
    }
}
