package com.nexo.manada_solidaria_backend.users.controllers.requests;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateRolesRequest(
        @NotNull(message = "Debe enviar la lista de roles")
        List<EditableRol> roles
) {

    public List<Rol> toRoles() {
        return roles.stream()
                .map(EditableRol::toRol)
                .toList();
    }
}
