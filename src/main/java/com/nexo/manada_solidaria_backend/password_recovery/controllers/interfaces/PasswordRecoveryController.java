package com.nexo.manada_solidaria_backend.password_recovery.controllers.interfaces;

import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.RequestRecoveryRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.ResetPasswordRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.requests.VerifyRecoveryCodeRequest;
import com.nexo.manada_solidaria_backend.password_recovery.controllers.responses.RecoveryTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Tag(name = "Password recovery")
@RequestMapping("/password-recovery")
public interface PasswordRecoveryController {

    @Operation(summary = "Request a recovery code for the account with the given email")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "The request was accepted, regardless of the email being registered"),
            @ApiResponse(responseCode = "400", description = "The email is missing or malformed")
    })
    @PostMapping("/request")
    @ResponseStatus(ACCEPTED)
    void requestRecovery(@RequestBody @Valid RequestRecoveryRequest request);

    @Operation(summary = "Validate a recovery code and get a single use token to reset the password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The code was valid"),
            @ApiResponse(responseCode = "400", description = "The code is wrong, expired, already used or out of attempts")
    })
    @PostMapping("/verify")
    RecoveryTokenResponse verifyCode(@RequestBody @Valid VerifyRecoveryCodeRequest request);

    @Operation(summary = "Set a new password using a recovery token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The password was updated"),
            @ApiResponse(responseCode = "400", description = "The token is invalid, expired or already used, or the password is not valid")
    })
    @PostMapping("/reset")
    @ResponseStatus(NO_CONTENT)
    void resetPassword(@RequestBody @Valid ResetPasswordRequest request);
}
