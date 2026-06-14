package com.nexo.manada_solidaria_backend.common.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public class MockBaseDataUtils {

    public static final String INVALID_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

    private static Stream<Arguments> providePingTestCases() {
        return Stream.of(
                Arguments.of("ping requests with valid access token", HttpStatus.OK, "pong"),
                Arguments.of("ping requests with invalid access token", HttpStatus.UNAUTHORIZED, null)
        );
    }
}
