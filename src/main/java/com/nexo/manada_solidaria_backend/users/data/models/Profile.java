package com.nexo.manada_solidaria_backend.users.data.models;

import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Profile {
    private String name;
    private String lastname;
    private String userNotificationURL;
    private String profileImageURL;
    private String email;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private List<Rol> roles = new ArrayList<>();
    @Id
    private UUID id = UUID.randomUUID();

    public Profile(String email, String phoneNumber, List<Rol> roles) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public void update(UpdateProfileRequest request) {
        this.name = request.name();
        this.lastname = request.lastname();
        this.email = request.email();
        this.phoneNumber = request.phoneNumber();
        this.profileImageURL = request.profileImageURL();
    }

    public void updateRoles(UpdateRolesRequest request) {
        List<Rol> updated = new ArrayList<>(request.toRoles());
        if (!updated.contains(Rol.RESCUER)) {
            updated.add(Rol.COMMUNITY);
        }
        this.roles = updated;
    }
}
