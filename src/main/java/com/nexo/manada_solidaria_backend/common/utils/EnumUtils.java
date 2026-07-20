package com.nexo.manada_solidaria_backend.common.utils;

public class EnumUtils {

    private EnumUtils() {
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
