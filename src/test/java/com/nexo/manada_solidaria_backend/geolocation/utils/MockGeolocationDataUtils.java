package com.nexo.manada_solidaria_backend.geolocation.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MockGeolocationDataUtils {

    public static final String GEOIP_OK_RESPONSE = """
            {
              "latitude": "-32.4075",
              "longitude": "-63.24",
              "city": "Villa Maria",
              "stateprov": "Cordoba",
              "stateprovCode": "X",
              "country": "AR",
              "timezone": "America/Argentina/Cordoba",
              "accuracyRadius": 50
            }
            """;

    public static final String GEOIP_INCOMPLETE_RESPONSE = """
            {
              "city": "Villa Maria",
              "stateprov": "Cordoba",
              "country": "AR"
            }
            """;

    private static Stream<Arguments> provideResponseFieldCases() {
        return Stream.of(
                Arguments.of("latitude", "$.latitude", is(-32.4075)),
                Arguments.of("longitude", "$.longitude", is(-63.24)),
                Arguments.of("city", "$.city", is("Villa Maria")),
                Arguments.of("region viene de stateprov", "$.region", is("Cordoba")),
                Arguments.of("country", "$.country", is("AR"))
        );
    }

    private static Stream<Arguments> provideClientIpHeaderCases() {
        return Stream.of(
                Arguments.of("Solo X-Forwarded-For", "181.30.72.1", null, "181.30.72.1"),
                Arguments.of("Solo X-Real-IP", null, "181.30.72.1", "181.30.72.1"),
                Arguments.of("Con ambos headers gana X-Forwarded-For", "181.30.72.1", "10.0.0.5", "181.30.72.1"),
                Arguments.of("X-Forwarded-For con cadena de proxies toma la primera IP",
                        "181.30.72.1, 10.0.0.5, 172.17.0.1", null, "181.30.72.1"),
                Arguments.of("IPv6 se envia URL-encodeada, sin corchetes",
                        "2803:9800:9842:8267:1c5b:e0d3:6a6d:8e2c", null,
                        "2803%3A9800%3A9842%3A8267%3A1c5b%3Ae0d3%3A6a6d%3A8e2c")
        );
    }

    private static Stream<Arguments> provideProviderErrorCases() {
        return Stream.of(
                Arguments.of("4xx del proveedor devuelve 404",
                        withStatus(HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND),
                Arguments.of("5xx del proveedor devuelve 502",
                        withStatus(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.BAD_GATEWAY),
                Arguments.of("Servicio caido devuelve 502",
                        withException(new IOException("connection refused")), HttpStatus.BAD_GATEWAY),
                Arguments.of("Respuesta con content type invalido devuelve 502",
                        withSuccess("<html>error</html>", MediaType.TEXT_HTML), HttpStatus.BAD_GATEWAY),
                Arguments.of("Respuesta sin coordenadas devuelve 404",
                        withSuccess(GEOIP_INCOMPLETE_RESPONSE, MediaType.APPLICATION_JSON), HttpStatus.NOT_FOUND)
        );
    }

    private static Stream<Arguments> provideUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Token invalido", INVALID_ACCESS_TOKEN)
        );
    }
}
