package com.nexo.manada_solidaria_backend.users.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public long getDaysSinceRegistration() {
        return Math.max(0, ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDate.now()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.profile.getRoles();
    }
}
