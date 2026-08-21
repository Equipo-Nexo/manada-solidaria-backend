package com.nexo.manada_solidaria_backend.common.controllers.responses;

import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;

public record PhoneNumberResponse(
        String areaCode,
        String number
) {

    public static PhoneNumberResponse from(PhoneNumber phoneNumber) {
        return phoneNumber == null
                ? null
                : new PhoneNumberResponse(phoneNumber.areaCode(), phoneNumber.number());
    }
}
