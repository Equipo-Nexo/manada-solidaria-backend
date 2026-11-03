package com.nexo.manada_solidaria_backend.common.controllers.requests;

import com.nexo.manada_solidaria_backend.common.data.enums.ReportableType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateReportPostRequest(
        @NotNull(message = "El ID de la publicación es obligatorio")
        UUID postId,

        @NotNull(message = "El tipo de publicación es obligatorio")
        ReportableType type
) {
}