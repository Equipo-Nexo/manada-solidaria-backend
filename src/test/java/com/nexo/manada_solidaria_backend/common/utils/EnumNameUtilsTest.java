package com.nexo.manada_solidaria_backend.common.utils;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumNameUtilsTest {

    @Test
    @DisplayName("nameOrNull devuelve el name() del enum")
    void nameOrNull_returnsEnumName() {
        assertThat(EnumNameUtils.nameOrNull(AnimalPostType.LOST)).isEqualTo("LOST");
    }

    @Test
    @DisplayName("nameOrNull devuelve null cuando el enum es null")
    void nameOrNull_returnsNullWhenValueIsNull() {
        assertThat(EnumNameUtils.nameOrNull(null)).isNull();
    }
}
