package com.nexo.manada_solidaria_backend.common.utils;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class
EnumUtilsTest {

    @Test
    @DisplayName("getNameOrNull devuelve el name() del enum")
    void getNameOrNull_returnsEnumName() {
        assertThat(EnumUtils.getNameOrNull(AnimalPostType.LOST)).isEqualTo("LOST");
    }

    @Test
    @DisplayName("getNameOrNull devuelve null cuando el enum es null")
    void getNameOrNull_returnsNullWhenValueIsNull() {
        assertThat(EnumUtils.getNameOrNull(null)).isNull();
    }
}
