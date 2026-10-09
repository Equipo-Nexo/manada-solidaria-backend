package com.nexo.manada_solidaria_backend.users.integrations;

import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseIntegrationTest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UsualLocationResponse;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class UsualLocationTest extends BaseIntegrationTest {

    private static final String MOCK_DATA = "com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils#";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("La ubicacion habitual es la zona donde mas se conecta entre sus ultimas 50 ubicaciones")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUsualLocationCases")
    void usualLocation(String testName, List<double[]> pointsFromOldest, double expectedLatitude, double expectedLongitude) {
        User admin = admin();
        saveLocations(admin, pointsFromOldest);

        UsualLocationResponse usual = userService.getUsualLocation(admin).orElseThrow();

        assertThat(usual.latitude()).isCloseTo(expectedLatitude, within(1e-9));
        assertThat(usual.longitude()).isCloseTo(expectedLongitude, within(1e-9));
    }

    @Test
    @DisplayName("Las ubicaciones de otro usuario no cuentan")
    void usualLocation_ignoresOtherUsers() {
        User other = userRepository.saveAndFlush(new User("viajero", "x",
                new Profile("viajero@mail.com", new PhoneNumber("353", "4014524"), Set.of(Rol.COMMUNITY))));
        saveLocations(other, Collections.nCopies(5, new double[]{-31.42, -64.19}));
        saveLocations(admin(), List.of(new double[]{-32.41, -63.24}));

        UsualLocationResponse usual = userService.getUsualLocation(admin()).orElseThrow();

        assertThat(usual.latitude()).isCloseTo(-32.41, within(1e-9));
    }

    @Test
    @DisplayName("Sin ubicaciones guardadas no hay ubicacion habitual")
    void usualLocation_withoutLocations_isEmpty() {
        assertThat(userService.getUsualLocation(admin())).isEmpty();
    }

    private void saveLocations(User user, List<double[]> pointsFromOldest) {
        LocalDateTime first = LocalDateTime.parse("2026-01-01T10:00:00");
        for (int i = 0; i < pointsFromOldest.size(); i++) {
            jdbcTemplate.update(
                    "INSERT INTO user_location (id, user_id, latitude, longitude, created_at) VALUES (?, ?, ?, ?, ?)",
                    UUID.randomUUID(), user.getId(), pointsFromOldest.get(i)[0], pointsFromOldest.get(i)[1], first.plusMinutes(i)
            );
        }
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }
}
