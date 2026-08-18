package com.nexo.manada_solidaria_backend.common.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PhoneNumber(
        @Column(length = 4)
        String areaCode,

        @Column(name = "phone_number", length = 7)
        String number
) {
}
