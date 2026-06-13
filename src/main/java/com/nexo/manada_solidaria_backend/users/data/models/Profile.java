package com.nexo.manada_solidaria_backend.users.data.models;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Rol> roles;
    @Id
    private UUID id = UUID.randomUUID();

}
