package com.nexo.manada_solidaria_backend.users.controllers.requests;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;

public enum EditableRol {
    RESCUER(Rol.RESCUER),
    TRANSITIONAL_HOME(Rol.TRANSITIONAL_HOME),
    CARRIAGE(Rol.CARRIAGE);

    private final Rol rol;

    EditableRol(Rol rol) {
        this.rol = rol;
    }

    public Rol toRol() {
        return rol;
    }
}
