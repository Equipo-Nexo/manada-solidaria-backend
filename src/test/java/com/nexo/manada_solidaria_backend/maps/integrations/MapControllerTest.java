package com.nexo.manada_solidaria_backend.maps.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import static com.nexo.manada_solidaria_backend.common.configs.ClockConfig.ARGENTINA;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MapControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA = "com.nexo.manada_solidaria_backend.maps.utils.MockMapDataUtils#";
    private static final LocalDateTime WEDNESDAY_AT_TEN = LocalDateTime.parse("2026-09-23T10:00");

    @Autowired
    private AnimalPostRepository animalPostRepository;
    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void freezeClock() {
        given(clock.instant()).willReturn(WEDNESDAY_AT_TEN.atZone(ARGENTINA).toInstant());
        given(clock.getZone()).willReturn(ARGENTINA);
    }

    @DisplayName("GET /map arma los perdidos, los en la calle y las veterinarias")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideMapFieldCases")
    @Sql("/sql/maps/map-data.sql")
    void getMap(String testName, String jsonPathExpression, Matcher<?> expected) throws Exception {
        mockMvc.perform(get("/map").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @DisplayName("GET /map marca la veterinaria abierta o cerrada segun su turno de hoy")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideOpeningHourCases")
    @Sql("/sql/maps/map-data.sql")
    void vetStatusFollowsOpeningHours(String testName, String time, String expectedStatus) throws Exception {
        given(clock.instant()).willReturn(WEDNESDAY_AT_TEN.with(LocalTime.parse(time)).atZone(ARGENTINA).toInstant());

        mockMvc.perform(get("/map").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vets[?(@.name == 'Dr. Seba Veterinaria')].status", contains(expectedStatus)));
    }

    @Test
    @DisplayName("Con el servidor en UTC, lo publicado a la noche en Argentina sigue siendo de hoy")
    void publishedAtNightWithServerInUtc() throws Exception {
        TimeZone serverZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        try {
            given(clock.instant()).willReturn(WEDNESDAY_AT_TEN.withHour(22).atZone(ARGENTINA).toInstant());
            saveLostPostCreatedAt(LocalDateTime.parse("2026-09-24T00:30"));

            mockMvc.perform(get("/map").header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.lostAnimals[0].firstLineDescription.text", is("0")));
        } finally {
            TimeZone.setDefault(serverZone);
        }
    }

    @DisplayName("GET /map sin credenciales validas devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUnauthorizedCases")
    void getMapUnauthorized(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/map");
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    private void saveLostPostCreatedAt(LocalDateTime createdAt) {
        User owner = userRepository.findByUsername("admin").orElseThrow();
        LostPost post = new LostPost(
                "Nocturno", "Se perdio a la noche", "/fotoNocturno", null,
                new PhoneNumber("353", "4010369"), true, owner,
                new Location("San Justo", "Constancio Vigil", 1821, -32.4102, -63.2411),
                new Animal(null, null, AnimalSize.MEDIUM, AnimalGender.MALE, AnimalType.DOG),
                null
        );
        post.setCreatedAt(createdAt);
        animalPostRepository.save(post);
    }
}
