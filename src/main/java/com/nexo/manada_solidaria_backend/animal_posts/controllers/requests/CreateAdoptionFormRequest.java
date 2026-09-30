package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateAdoptionFormRequest(
        @NotNull(message = "El id de la publicación es obligatorio")
        UUID adoptionPostId,

        @Valid
        @NotNull(message = "El número de teléfono es obligatorio")
        PhoneNumberRequest phoneNumber,

        @Valid
        @NotEmpty(message = "Debe responder al menos una pregunta")
        List<QuestionFormRequest> questions
) {

    public record QuestionFormRequest(
            @NotNull(message = "El id de la pregunta es obligatorio")
            UUID questionFormId,

            @Size(max = 1000, message = "La respuesta no puede superar los 1000 caracteres")
            String answer
    ) {}
}