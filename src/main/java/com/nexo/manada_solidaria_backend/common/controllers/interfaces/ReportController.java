package com.nexo.manada_solidaria_backend.common.controllers.interfaces;

import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Reports", description = "Endpoints para la moderación y reporte de publicaciones")
@RequestMapping("/reports")
public interface ReportController {

    @Operation(summary = "Reportar una publicación o campaña")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reporte registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Petición inválida o intento de reportar una publicación propia"),
            @ApiResponse(responseCode = "404", description = "La publicación o campaña no existe"),
            @ApiResponse(responseCode = "409", description = "El usuario ya ha reportado esta publicación anteriormente")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void reportPost(
            @Valid @RequestBody CreateReportPostRequest request,
            @AuthenticationPrincipal User authenticatedUser
    );
}