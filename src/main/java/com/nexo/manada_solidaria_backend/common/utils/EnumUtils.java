package com.nexo.manada_solidaria_backend.common.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

public class EnumUtils {

    private EnumUtils() {
    }

    public static <E extends Enum<E>> E parseOrThrow(Class<E> type, String value) {
        return Arrays.stream(type.getEnumConstants())
                .filter(constant -> constant.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El valor '" + value + "' no es válido. Los valores permitidos son: "
                                + Arrays.toString(type.getEnumConstants())
                ));
    }

    /**
     * Devuelve el name() del enum, o null si el enum es null.
     *
     * @param value
     * @return String con el name() del enum, o null si el enum es null.
     */
    public static String getNameOrNull(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
