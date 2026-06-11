package com.nexo.manada_solidaria_backend.animal_posts.controllers;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.implementations.AnimalPostControllerImpl;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostType;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.responses.AnimalPostResponse;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.auth.components.BearerTokenConverter;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AnimalPostControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class AnimalPostControllerTest {

    private static final UUID POST_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private AnimalPostService animalPostService;

    /*
     * SecurityConfig (sin @Profile) carga beans de auth con deps que @WebMvcTest no satisface.
     * Con addFilters=false los filtros no corren, pero los beans deben existir para que el contexto arranque.
     */
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private BearerTokenConverter bearerTokenConverter;
    @MockitoBean
    private AuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    @Test
    @DisplayName("POST /animal-post LOST válido devuelve 201 con cuerpo")
    void shouldCreateLostPost() {
        given(animalPostService.create(any(), any())).willReturn(sampleResponse(AnimalPostType.LOST));

        var json = """
                {
                  "type": "LOST",
                  "title": "Perdí a mi perro",
                  "description": "Se escapó en el parque",
                  "imageId": "cf-image-123",
                  "hasOwner": true,
                  "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
                  "location": { "name": "Parque Centenario", "address": "Av. Patricias", "number": 100, "latitude": -34.6, "longitude": -58.4 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.status").isEqualTo("CREATED");
    }

    @Test
    @DisplayName("POST /animal-post ADOPTION válido devuelve 201")
    void shouldCreateAdoptionPost() {
        given(animalPostService.create(any(), any())).willReturn(sampleResponse(AnimalPostType.ADOPTION));

        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Busco hogar para gata",
                  "description": "Rescatada de la calle",
                  "imageId": "cf-image-456",
                  "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
                  "location": { "name": "Refugio Norte", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.type").isEqualTo("ADOPTION");
    }

    @Test
    @DisplayName("POST /animal-post sin título devuelve 400")
    void shouldRejectMissingTitle() {
        var json = """
                {
                  "type": "ADOPTION",
                  "description": "Sin título",
                  "imageId": "cf-image-789",
                  "animal": { "type": "DOG", "size": "LARGE", "gender": "UNKNOWN" },
                  "location": { "name": "Plaza", "address": "Mitre", "number": 1, "latitude": -34.0, "longitude": -58.0 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post LOST sin hasOwner devuelve 400")
    void shouldRejectLostPostWithoutHasOwner() {
        var json = """
                {
                  "type": "LOST",
                  "title": "Encontré un perro",
                  "description": "Estaba solo en la calle",
                  "imageId": "cf-image-999",
                  "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
                  "location": { "name": "Esquina", "address": "Corrientes", "number": 500, "latitude": -34.6, "longitude": -58.4 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post LOST con hasOwner=false (animal encontrado) devuelve 201")
    void shouldCreateLostPostFoundOnStreet() {
        given(animalPostService.create(any(), any())).willReturn(sampleResponse(AnimalPostType.LOST));

        var json = """
                {
                  "type": "LOST",
                  "title": "Encontré un perro en la calle",
                  "description": "Estaba solo, sin collar",
                  "imageId": "cf-image-found-001",
                  "hasOwner": false,
                  "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
                  "location": { "name": "Parque Rivadavia", "address": "Av. Rivadavia", "number": 4900, "latitude": -34.6, "longitude": -58.4 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.type").isEqualTo("LOST");
    }

    @Test
    @DisplayName("POST /animal-post sin type devuelve 400")
    void shouldRejectMissingType() {
        var json = """
                {
                  "title": "Sin tipo",
                  "description": "Descripción válida",
                  "imageId": "cf-image-000",
                  "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
                  "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post sin description devuelve 400")
    void shouldRejectMissingDescription() {
        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Gatito en adopción",
                  "imageId": "cf-image-111",
                  "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
                  "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post sin imageId devuelve 400")
    void shouldRejectMissingImageId() {
        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Gatito en adopción",
                  "description": "Rescatado de la calle",
                  "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
                  "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post sin animal devuelve 400")
    void shouldRejectMissingAnimal() {
        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Post sin animal",
                  "description": "Descripción válida",
                  "imageId": "cf-image-222",
                  "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /animal-post sin location devuelve 400")
    void shouldRejectMissingLocation() {
        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Post sin ubicación",
                  "description": "Descripción válida",
                  "imageId": "cf-image-333",
                  "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    private AnimalPostResponse sampleResponse(AnimalPostType type) {
        var animal = new AnimalPostResponse.AnimalResponse(
                UUID.randomUUID(), AnimalType.DOG, AnimalSize.MEDIUM, AnimalGender.MALE,
                null, null, null, null, null);
        var location = new AnimalPostResponse.LocationResponse(
                UUID.randomUUID(), "Parque", "Av", 100, -34.6, -58.4);
        return new AnimalPostResponse(
                POST_ID, type, "Título", "Descripción", "cf-image-123",
                animal, location, "CREATED", LocalDateTime.now(), null);
    }
}
