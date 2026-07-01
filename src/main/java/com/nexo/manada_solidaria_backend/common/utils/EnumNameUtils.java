package com.nexo.manada_solidaria_backend.common.utils;

public class EnumNameUtils {

    private EnumNameUtils() {
    }

    // Devuelve el name() del enum, o null si el enum es null (para filtros opcionales).
    public static String nameOrNull(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
