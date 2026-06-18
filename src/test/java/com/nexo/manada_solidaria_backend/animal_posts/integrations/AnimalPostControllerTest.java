package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
class AnimalPostControllerTest extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private AnimalPostRepository animalPostRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("POST /animal-post — código de estado por payload")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideCreateCases")
    void createTests(
            String testName,
            String body,
            HttpStatus expectedStatus,
            String expectedType
    ) throws Exception {
        var result = mockMvc.perform(
                post("/animal-post")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().is(expectedStatus.value()));

        if (expectedStatus.is2xxSuccessful()) {
            result.andExpect(jsonPath("$.type").value(expectedType));
        }
    }

    @Test
    @DisplayName("POST /animal-post válido: persiste, refleja el input y queda con el owner del JWT")
    void create_persistsPostWithAuthenticatedOwnerAndInputData() throws Exception {
        UUID adminId = userRepository.findByUsername("admin").orElseThrow().getId();

        String responseBody = mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("LOST"))
                .andExpect(jsonPath("$.title").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.description").value("Se escapó en el parque"))
                .andExpect(jsonPath("$.imageUrl").value("cf-image-123")) // imageId del request → imageUrl
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.animal.type").value("DOG"))
                .andExpect(jsonPath("$.animal.size").value("MEDIUM"))
                .andExpect(jsonPath("$.animal.gender").value("MALE"))
                .andExpect(jsonPath("$.animal.age").value("ADULT"))
                .andExpect(jsonPath("$.location.name").value("Parque Centenario"))
                .andExpect(jsonPath("$.location.address").value("Av. Patricias"))
                .andExpect(jsonPath("$.location.number").value(100))
                .andExpect(jsonPath("$.phoneNumber").value("1122334455"))
                .andExpect(jsonPath("$.reward").value(5000))
                // El owner NO viene en el payload: se resuelve del JWT autenticado.
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(responseBody).get("id").asText());
        AnimalPost saved = animalPostRepository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("Perdí a mi perro");
        assertThat(saved.getOwner().getId()).isEqualTo(adminId);
    }

    @Test
    @DisplayName("POST /animal-post LOST sin hasOwner: 400 con el mensaje del validator @RequiredFieldsByType")
    void create_lostWithoutHasOwner_returnsValidatorMessage() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_WITHOUT_HAS_OWNER)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("hasOwner es obligatorio"))));
    }

    @Test
    @DisplayName("POST /animal-post sin token devuelve 401")
    void create_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /animal-post con token inválido devuelve 401")
    void create_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /animal-post ADOPTION: inTransit define el estado vigente y cierra el CREATED inicial")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideInTransitCases")
    void create_adoption_transitionsByInTransit(String testName, String body, String expectedStatus) throws Exception {
        String responseBody = mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ADOPTION"))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(responseBody).get("id").asText());
        AdoptionPost saved = (AdoptionPost) animalPostRepository.findById(id).orElseThrow();

        // Las dos filas son exactamente CREATED + el estado esperado (descarta el falso positivo "dos CREATED").
        assertThat(saved.getStatusHistory())
                .extracting(h -> h.getStatus().name())
                .containsExactlyInAnyOrder("CREATED", expectedStatus);
        // El CREATED inicial quedó cerrado.
        assertThat(saved.getStatusHistory())
                .filteredOn(h -> h.getStatus() == StatusAdoptionPost.CREATED)
                .singleElement()
                .satisfies(h -> assertThat(h.getFinishedAt()).isNotNull());
        // El estado vigente (abierto) es el esperado.
        assertThat(saved.getCurrentStatus().getStatus().name()).isEqualTo(expectedStatus);
        assertThat(saved.getCurrentStatus().getFinishedAt()).isNull();
    }
}
