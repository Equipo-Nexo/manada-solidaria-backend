package com.nexo.manada_solidaria_backend.users.utils;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabasePopulateTest {

    @Test
    @DisplayName("La contrasena que se siembra para el admin cumple la politica de contrasena")
    void seededAdminPasswordMeetsThePasswordPolicy() {
        CreateUserRequest admin = CreateUserRequest.builder()
                .username(DatabasePopulate.ADMIN_USERNAME)
                .password(DatabasePopulate.ADMIN_PASSWORD)
                .repeatedPassword(DatabasePopulate.ADMIN_PASSWORD)
                .email("admin@mail.com")
                .build();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(admin)).isEmpty();
        }
    }
}
