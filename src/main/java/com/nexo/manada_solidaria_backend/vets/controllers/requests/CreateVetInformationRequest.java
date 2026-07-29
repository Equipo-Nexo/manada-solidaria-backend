package com.nexo.manada_solidaria_backend.vets.controllers.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record CreateVetInformationRequest(

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
        LocationRequest location,

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
            DayOfWeek dayOfWeek,
            LocalTime openingTime,
            LocalTime closingTime
    ) {
    }

}