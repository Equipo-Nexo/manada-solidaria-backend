package com.nexo.manada_solidaria_backend.common.controllers.validations;

public class PhoneValidation {

    public static final String AREA_CODE_REGEX = "\\d{3,4}";
    public static final String AREA_CODE_MESSAGE = "El código de área debe tener 3 o 4 dígitos";
    public static final String PHONE_NUMBER_REGEX = "\\d{6,7}";
    public static final String PHONE_NUMBER_MESSAGE = "El número de teléfono debe tener 6 o 7 dígitos";

    private PhoneValidation() {
    }
}
