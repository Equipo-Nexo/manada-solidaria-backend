package com.nexo.manada_solidaria_backend.users.data.models;

import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
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
    @Column(unique = true)
    private String email;
    @Embedded
    private PhoneNumber phoneNumber;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "profile_roles",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Rol> roles = new HashSet<>();
    @Id
    private UUID id = UUID.randomUUID();

    public Profile(String email, PhoneNumber phoneNumber, Set<Rol> roles) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public void update(UpdateProfileRequest request) {
        this.name = request.name();
        this.lastname = request.lastname();
        this.email = request.email();
        this.phoneNumber = PhoneNumberRequest.toDomain(request.phoneNumber());
        this.profileImageURL = request.profileImageURL();
    }

    public boolean hasRole(Rol role) {
        return roles.contains(role);
    }

    public void updateRoles(UpdateRolesRequest request) {
        Set<Rol> updated = new HashSet<>(request.toRoles());
        if (!updated.contains(Rol.RESCUER)) {
            updated.add(Rol.COMMUNITY);
        }
        this.roles = updated;
    }
}
