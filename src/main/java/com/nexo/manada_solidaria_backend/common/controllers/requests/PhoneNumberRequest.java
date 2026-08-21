package com.nexo.manada_solidaria_backend.common.controllers.requests;

import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneNumberRequest(
        @NotBlank(message = "El código de área es obligatorio")
        @Pattern(
                regexp = "\\d{3,4}",
                message = "El código de área debe tener 3 o 4 dígitos"
        )
        String areaCode,

        @NotBlank(message = "El número de teléfono es obligatorio")
        @Pattern(
                regexp = "\\d{6,7}",
                message = "El número de teléfono debe tener 6 o 7 dígitos"
        )
        String number
) {

    public static PhoneNumber toDomain(PhoneNumberRequest request) {
        return request == null ? null : new PhoneNumber(request.areaCode(), request.number());
    }
}
