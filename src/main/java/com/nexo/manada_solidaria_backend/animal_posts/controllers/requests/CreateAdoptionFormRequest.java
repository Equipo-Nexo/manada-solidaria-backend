package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateAdoptionFormRequest(
        @NotNull(message = "El id de la publicación es obligatorio")
        UUID adoptionPostId,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        @Valid
        @NotNull(message = "El número de teléfono es obligatorio")
        PhoneNumberRequest phoneNumber,

        @Valid
        @NotEmpty(message = "Debe responder al menos una pregunta")
        List<AnswerFormRequest> answers
) {

    public record AnswerFormRequest(
            @NotNull(message = "El id de la pregunta es obligatorio")
            UUID questionId,

            @Size(max = 1000, message = "La respuesta no puede superar los 1000 caracteres")
            String answer
    ) {}
}