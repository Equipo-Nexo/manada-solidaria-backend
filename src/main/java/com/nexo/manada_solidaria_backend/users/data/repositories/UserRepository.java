package com.nexo.manada_solidaria_backend.users.data.repositories;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN u.profile.roles r
        WHERE r = :role
    """)
    List<User> findAllByRole(@Param("role") Rol role);
}
