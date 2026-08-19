package com.nexo.manada_solidaria_backend.animal_posts.controllers.requests;

import jakarta.validation.constraints.NotBlank;

public record TransitionStatusRequest(
        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
