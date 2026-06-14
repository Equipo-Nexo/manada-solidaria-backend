package com.nexo.manada_solidaria_backend.auth.controllers.interfaces;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication")
@RequestMapping("/auth")
public interface AuthController {

    @Operation(
            summary = "Log in with an registered user",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The user has logged in correctly",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )}
            ),
            @ApiResponse(responseCode = "401", description = "Credentials are wrong")
    })
    @PostMapping("/login")
    LoginResponse login(@RequestHeader("Authorization") String credentials);

    @PostMapping("/signup")
    void signup(@RequestBody @Valid CreateUserRequest createUserRequest);

}
