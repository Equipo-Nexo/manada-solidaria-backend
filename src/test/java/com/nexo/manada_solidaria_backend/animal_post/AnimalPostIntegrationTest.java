package com.nexo.manada_solidaria_backend.animal_post;

import com.nexo.manada_solidaria_backend.animal_post.data.repositories.AnimalPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class AnimalPostIntegrationTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private AnimalPostRepository animalPostRepository;

    @BeforeEach
    void cleanDatabase() {
        animalPostRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /animal-post LOST persiste en MySQL y se puede recuperar por ID")
    void shouldPersistLostPostToMySQL() {
        var json = """
                {
                  "type": "LOST",
                  "title": "Perdí a mi perro",
                  "description": "Se escapó en el parque",
                  "imageId": "cf-img-integ-001",
                  "hasOwner": true,
                  "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
                  "location": { "name": "Parque Centenario", "address": "Av. Patricias Argentinas", "number": 100, "latitude": -34.606, "longitude": -58.435 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id")
                .satisfies(rawId -> {
                    var id = UUID.fromString((String) rawId);
                    assertThat(animalPostRepository.findById(id)).isPresent();
                });
    }

    @Test
    @DisplayName("POST /animal-post ADOPTION persiste en MySQL y se puede recuperar por ID")
    void shouldPersistAdoptionPostToMySQL() {
        var json = """
                {
                  "type": "ADOPTION",
                  "title": "Gatita en adopción",
                  "description": "Rescatada de la calle",
                  "imageId": "cf-img-integ-002",
                  "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
                  "location": { "name": "Refugio Norte", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
                }
                """;

        assertThat(mvc.post().uri("/animal-post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id")
                .satisfies(rawId -> {
                    var id = UUID.fromString((String) rawId);
                    assertThat(animalPostRepository.findById(id)).isPresent();
                });
    }
}
