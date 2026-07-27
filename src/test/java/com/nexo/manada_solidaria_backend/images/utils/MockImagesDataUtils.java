package com.nexo.manada_solidaria_backend.images.utils;

import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public class MockImagesDataUtils {
    private static Stream<Arguments> provideGetPresignedUrlTestCases() {
        return Stream.of(
                Arguments.of("Request is OK", new CreatePresignedUrlRequest("image/jpg", 5000), HttpStatus.OK),
                Arguments.of("ContentType is not supported", new CreatePresignedUrlRequest("images/jpg", 5000), HttpStatus.BAD_REQUEST),
                Arguments.of("File size is too big", new CreatePresignedUrlRequest("image/jpg", 10000000), HttpStatus.BAD_REQUEST)
        );
    }
}
