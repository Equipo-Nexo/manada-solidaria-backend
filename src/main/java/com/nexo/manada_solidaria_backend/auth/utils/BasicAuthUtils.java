package com.nexo.manada_solidaria_backend.auth.utils;

import java.util.Base64;

import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.BASIC_PREFIX;
import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.COLON_SYMBOL;

public class BasicAuthUtils {


    public static String extractUsername(String authorization) {
        return getCredentials(authorization)[0];
    }

    private static String[] getCredentials(String authorization) {
        return decodeAuthorizationHeader(authorization).split(COLON_SYMBOL);
    }

    private static String decodeAuthorizationHeader(String authorization) {
        return new String(
                decodeString(
                        authorization
                                .substring(BASIC_PREFIX.length())
                )
        );
    }

    private static byte[] decodeString(String encodedCredentials) {
        return Base64.getDecoder().decode(encodedCredentials);
    }
}
