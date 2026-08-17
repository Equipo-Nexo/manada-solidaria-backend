package com.nexo.manada_solidaria_backend.vets.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record CreateVetInformationRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "El teléfono es obligatorio")
        @Valid
        PhoneNumberRequest phoneNumber,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        String email,

        String profilePictureUrl,

        String vetPageUrl,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description,

        @NotNull(message = "La ubicación es obligatoria")
        @Valid
        LocationRequest location,

        @NotEmpty(message = "El calendario es obligatorio y debe contener al menos un horario")
        @Valid
        List<ScheduleRequest> calendar

) {

    public record LocationRequest(

            @NotBlank(message = "El nombre de la ubicación es obligatorio")
            String name,

            String address,

            Integer number,

            @NotNull(message = "La latitud es obligatoria")
            Double latitude,

            @NotNull(message = "La longitud es obligatoria")
            Double longitude

    ) {
    }

    public record ScheduleRequest(
            @NotNull(message = "El día es obligatorio")
            DayOfWeek dayOfWeek,

            @NotNull(message = "La hora de apertura es obligatoria")
            LocalTime openingTime,

            @NotNull(message = "La hora de cierre es obligatoria")
            LocalTime closingTime
    ) {
    }

}