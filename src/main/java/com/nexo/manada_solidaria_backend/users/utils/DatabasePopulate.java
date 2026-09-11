package com.nexo.manada_solidaria_backend.users.utils;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@org.springframework.context.annotation.Profile("local | development")
@Configuration
public class DatabasePopulate {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "Admin123!";

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User admin = userRepository.findByUsername(ADMIN_USERNAME)
                    .orElseGet(DatabasePopulate::newAdmin);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            userRepository.save(admin);
        };
    }

    private static User newAdmin() {
        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        Profile profile = new Profile();
        profile.setRoles(List.of(Rol.COMMUNITY));
        admin.setProfile(profile);
        return admin;
    }
}
