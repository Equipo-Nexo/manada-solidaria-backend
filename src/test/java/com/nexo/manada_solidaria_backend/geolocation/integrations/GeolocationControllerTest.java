package com.nexo.manada_solidaria_backend.geolocation.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils.emptyResponse;
import static com.nexo.manada_solidaria_backend.geolocation.utils.MockGeolocationDataUtils.twoFeaturesResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeolocationControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String GEOAPIFY_API_KEY = "geoapify-test-key";
    private static final String GEOAPIFY_AUTOCOMPLETE_PATH = "/v1/geocode/autocomplete";
    private static final String GEOAPIFY_REVERSE_PATH = "/v1/geocode/reverse";

    @Test
    @DisplayName("GET /geolocation devuelve las ubicaciones de Geoapify y usa el limite por defecto")
    void autocomplete_returnsMappedLocationsAndUsesDefaultLimit() throws Exception {
        enqueueJsonResponse(GEOAPIFY_AUTOCOMPLETE_PATH, twoFeaturesResponse());

        mockMvc.perform(
                        get("/geolocation")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("text", "Buenos Aires")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].country").value("Argentina"))
                .andExpect(jsonPath("$[0].city").value("Buenos Aires"))
                .andExpect(jsonPath("$[0].street").value("Avenida Corrientes"))
                .andExpect(jsonPath("$[0].housenumber").value("100"))
                .andExpect(jsonPath("$[0].lon").value(-58.3816))
                .andExpect(jsonPath("$[0].lat").value(-34.6037))
                .andExpect(jsonPath("$[0].result_type").value("amenity"))
                .andExpect(jsonPath("$[0].formatted").value("Avenida Corrientes 100, Buenos Aires"))
                .andExpect(jsonPath("$[1].city").value("La Plata"));

        RecordedRequest request = takeMockServerRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getRequestUrl().encodedPath()).isEqualTo("/v1/geocode/autocomplete");
        assertThat(request.getRequestUrl().queryParameter("apiKey")).isEqualTo(GEOAPIFY_API_KEY);
        assertThat(request.getRequestUrl().queryParameter("text")).isEqualTo("Buenos Aires");
        assertThat(request.getRequestUrl().queryParameter("limit")).isEqualTo("5");
        assertThat(request.getRequestUrl().queryParameter("bias")).isNull();
    }

    @Test
    @DisplayName("GET /geolocation envia limite y proximidad a Geoapify")
    void autocomplete_withCoordinatesSendsLimitAndBias() throws Exception {
        enqueueJsonResponse(GEOAPIFY_AUTOCOMPLETE_PATH, emptyResponse());

        mockMvc.perform(
                        get("/geolocation")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("text", "Plaza")
                                .param("limit", "3")
                                .param("longitude", "-58.3816")
                                .param("latitude", "-34.6037")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        RecordedRequest request = takeMockServerRequest();
        assertThat(request.getRequestUrl().queryParameter("limit")).isEqualTo("3");
        assertThat(request.getRequestUrl().queryParameter("bias"))
                .isEqualTo("proximity:-58.3816,-34.6037");
    }

    @Test
    @DisplayName("GET /geolocation/reverse devuelve la primera ubicacion de Geoapify")
    void reverse_returnsFirstMappedLocation() throws Exception {
        enqueueJsonResponse(GEOAPIFY_REVERSE_PATH, twoFeaturesResponse());

        mockMvc.perform(
                        get("/geolocation/reverse")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("latitude", "-34.6037")
                                .param("longitude", "-58.3816")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Argentina"))
                .andExpect(jsonPath("$.city").value("Buenos Aires"))
                .andExpect(jsonPath("$.address_line1").value("Avenida Corrientes 100"))
                .andExpect(jsonPath("$.address_line2").value("Buenos Aires, Argentina"));

        RecordedRequest request = takeMockServerRequest();
        assertThat(request.getRequestUrl().encodedPath()).isEqualTo("/v1/geocode/reverse");
        assertThat(request.getRequestUrl().queryParameter("apiKey")).isEqualTo(GEOAPIFY_API_KEY);
        assertThat(request.getRequestUrl().queryParameter("lat")).isEqualTo("-34.6037");
        assertThat(request.getRequestUrl().queryParameter("lon")).isEqualTo("-58.3816");
    }

    @Test
    @DisplayName("GET /geolocation/reverse devuelve body vacio cuando Geoapify no encuentra resultados")
    void reverse_whenNoFeatureReturnsEmptyBody() throws Exception {
        enqueueJsonResponse(GEOAPIFY_REVERSE_PATH, emptyResponse());

        mockMvc.perform(
                        get("/geolocation/reverse")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("latitude", "-34.6037")
                                .param("longitude", "-58.3816")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        takeMockServerRequest();
    }

    @Test
    @DisplayName("Los endpoints de geolocation devuelven 502 cuando Geoapify falla")
    void autocomplete_whenProviderFailsReturnsBadGateway() throws Exception {
        enqueueResponse(GEOAPIFY_AUTOCOMPLETE_PATH, new MockResponse().setResponseCode(503));

        mockMvc.perform(
                        get("/geolocation")
                                .header("Authorization", "Bearer " + accessToken)
                                .param("text", "Buenos Aires")
                )
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.errors[0]")
                        .value("El servicio de geolocalizacion no se encuentra disponible."));

        takeMockServerRequest();
    }

    @Test
    @DisplayName("GET /geolocation requiere autenticacion")
    void autocomplete_withoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/geolocation").param("text", "Buenos Aires"))
                .andExpect(status().isUnauthorized());
    }

}
