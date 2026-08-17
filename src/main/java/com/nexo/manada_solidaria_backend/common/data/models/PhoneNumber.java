package com.nexo.manada_solidaria_backend.common.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(
        String areaCode,

        @Column(name = "phone_number")
        String number
) {
}
