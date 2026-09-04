package com.nexo.manada_solidaria_backend.guards.controllers.interfaces;

import com.nexo.manada_solidaria_backend.guards.controllers.responses.GuardStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Guards")
@RequestMapping("/guards")
public interface GuardController {

    @Operation(summary = "Tells whether the veterinary emergency guard is active right now")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The current state of the guard"),
            @ApiResponse(responseCode = "401", description = "The access token is missing or invalid")
    })
    @GetMapping("/status")
    GuardStatusResponse getStatus();
}
