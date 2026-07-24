package com.nexo.manada_solidaria_backend.images.controllers.interfaces;

import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import com.nexo.manada_solidaria_backend.images.controllers.responses.PresignedUrlResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/images")
public interface ImageController {
    @PostMapping("/presigned-url")
    PresignedUrlResponse getPresignedUrl(@RequestBody CreatePresignedUrlRequest createPresignedUrlRequest);
}
