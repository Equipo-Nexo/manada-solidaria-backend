package com.nexo.manada_solidaria_backend.geolocation.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GeolocationControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String GEOIP_BASE_URL = "http://localhost:8081";
    private static final String CLIENT_IP = "181.30.72.1";

    @Autowired
    private MockRestServiceServer geoIpMockServer;

    @TestConfiguration
    static class GeoIpMockConfig {
        private final RestClient.Builder builder = RestClient.builder().baseUrl(GEOIP_BASE_URL);

        @Bean
        MockRestServiceServer geoIpMockServer() {
            return MockRestServiceServer.bindTo(builder).build();
        }

        @Bean
        @Primary
        RestClient testGeoIpRestClient(MockRestServiceServer geoIpMockServer) {
            return builder.build();
        }
    }

    @BeforeEach
    void resetGeoIpServer() {
        geoIpMockServer.reset();
    }

    @DisplayName("GET /geolocation mapea cada campo de la respuesta del servicio GeoIP")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils#provideResponseFieldCases")
    void locate_mapsGeoIpResponseFields(String testName, String jsonPathExpression, Matcher<?> expected) throws Exception {
        geoIpMockServer
                .expect(requestTo(GEOIP_BASE_URL + "/" + CLIENT_IP))
                .andRespond(withSuccess(MockGeolocationDataUtils.GEOIP_OK_RESPONSE, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/geolocation")
                        .header("X-Forwarded-For", CLIENT_IP)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @DisplayName("GET /geolocation resuelve la IP del cliente desde los headers del proxy")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils#provideClientIpHeaderCases")
    void locate_resolvesClientIpFromHeaders(String testName, String forwardedFor, String realIp, String expectedIp) throws Exception {
        geoIpMockServer
                .expect(requestTo(GEOIP_BASE_URL + "/" + expectedIp))
                .andRespond(withSuccess(MockGeolocationDataUtils.GEOIP_OK_RESPONSE, MediaType.APPLICATION_JSON));

        MockHttpServletRequestBuilder request = get("/geolocation")
                .header("Authorization", "Bearer " + accessToken);
        if (forwardedFor != null) {
            request = request.header("X-Forwarded-For", forwardedFor);
        }
        if (realIp != null) {
            request = request.header("X-Real-IP", realIp);
        }

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /geolocation sin los headers del proxy devuelve 400")
    void locate_withoutProxyHeaders_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/geolocation")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /geolocation mapea cada fallo del proveedor GeoIP al status correspondiente")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils#provideProviderErrorCases")
    void locate_providerError_mapsStatus(String testName, ResponseCreator providerResponse, HttpStatus expectedStatus) throws Exception {
        geoIpMockServer
                .expect(requestTo(GEOIP_BASE_URL + "/" + CLIENT_IP))
                .andRespond(providerResponse);

        mockMvc.perform(get("/geolocation")
                        .header("X-Forwarded-For", CLIENT_IP)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is(expectedStatus.value()));
    }

    @DisplayName("GET /geolocation sin credenciales validas devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils#provideUnauthorizedCases")
    void locate_unauthorized_returns401(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/geolocation")
                .header("X-Forwarded-For", CLIENT_IP);
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }
}
