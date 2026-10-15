package com.nexo.manada_solidaria_backend.users.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;
    @Column(updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Id
    private final UUID id = UUID.randomUUID();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.profile.getRoles();
    }

    public String getFullNameOrUsername() {
        if (hasValidProfileName()) {
            return this.profile.getFullName();
        }
        return this.username;
    }

    private boolean hasValidProfileName() {
        return this.profile != null && this.profile.hasFullName();
    }
}