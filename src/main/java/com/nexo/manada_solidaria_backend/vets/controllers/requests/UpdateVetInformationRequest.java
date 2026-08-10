package com.nexo.manada_solidaria_backend.vets.controllers.requests;

import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record UpdateVetInformationRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(
                regexp = "^[0-9]{8,15}$",
                message = "El teléfono debe contener entre 8 y 15 dígitos numéricos"
        )
        String phone,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        String email,

        String profilePictureUrl,

        String vetPageUrl,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description,

        @NotNull(message = "La ubicación es obligatoria")
        @Valid
        UpdateLocationRequest location,

        @NotEmpty(message = "El calendario es obligatorio y debe contener al menos un horario")
        @Valid
        List<ScheduleRequest> calendar

) {

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