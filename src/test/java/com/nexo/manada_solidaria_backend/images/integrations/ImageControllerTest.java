package com.nexo.manada_solidaria_backend.images.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.images.controllers.requests.CreatePresignedUrlRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageControllerTest extends BaseAuthenticatedIntegrationTest {

    @DisplayName("Get presigned url")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.images.utils.MockImagesDataUtils#provideGetPresignedUrlTestCases")
    void getPresignedUrl(
            String testName,
            CreatePresignedUrlRequest request,
            HttpStatus expectedStatusCode
    ) throws Exception {
        mockMvc.perform(
                        post("/images/presigned-url")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(toJson(request))
                )
                .andExpect(status().is(expectedStatusCode.value()));
    }
}
