package com.nexo.manada_solidaria_backend.auth.controllers.requests;

import com.nexo.manada_solidaria_backend.auth.validations.annotations.PasswordMatches;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@PasswordMatches
public class CreateUserRequest {
    @NotBlank(message = "Debe ingresar un nombre de usuario")
    @Size(min = 4, message = "El nombre de usuario debe tener minimo 4 caracteres")
    private String username;
    @NotBlank(message = "Debe ingresar una contraseña")
    @Size(min = 6, message = "la contraseña debe tener al menos 6 caracteres")
    private String password;
    @NotBlank(message = "Debe repetir la contraseña")
    private String repeatedPassword;
    private List<Rol> roles;
    @Email
    @NotNull(message = "Debe ingresar un correo electrónico")
    private String email;
    @Valid
    private PhoneNumberRequest phoneNumber;
}
