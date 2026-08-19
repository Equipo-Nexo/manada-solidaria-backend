package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.TransitionStatusRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalAge;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AnimalPostControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#";

    @Autowired
    private AnimalPostRepository animalPostRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("POST /animal-post — código de estado por payload")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateCases")
    void createTests(
            String testName,
            String body,
            HttpStatus expectedStatus,
            String expectedType
    ) throws Exception {
        var result = mockMvc.perform(
                post("/animal-posts")
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

        mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("LOST"))
                .andExpect(jsonPath("$.name").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.description").value("Se escapó en el parque"))
                .andExpect(jsonPath("$.imageUrl").value("cf-image-123")) // imageId del request → imageUrl
                .andExpect(jsonPath("$.status").value("SEARCHING"))
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
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()));

        // Y realmente quedó en la BBDD: se recupera por el GET con los mismos datos y dueño.
        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.content[0].ownerId").value(adminId.toString()))
                .andExpect(jsonPath("$.content[0].status").value("SEARCHING"));
    }

    @DisplayName("La response distingue perdido de en la calle sin exponer hasOwner")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideResponseTypeCases")
    void response_typeDistinguishesEachCategory(String testName, String body, String expectedType) throws Exception {
        mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(expectedType));
    }

    @Test
    @DisplayName("POST /animal-post LOST sin hasOwner: 400 con el mensaje del validator @ConditionalField")
    void create_lostWithoutHasOwner_returnsValidatorMessage() throws Exception {
        mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_WITHOUT_HAS_OWNER)
                )
                .andExpect(status().isBadRequest())
                // Substring ASCII: robusto ante la normalización de tildes del repo al commitear.
                .andExpect(jsonPath("$.errors", hasItem(containsString("hasOwner es obligatorio"))));
    }

    @DisplayName("POST /animal-post — campos opcionales y errores de enum")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateFieldCases")
    void createFieldTests(
            String testName,
            String body,
            HttpStatus expectedStatus,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("POST /animal-post sin token devuelve 401")
    void create_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /animal-post con token inválido devuelve 401")
    void create_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /animal-post ADOPTION: inTransit define el estado vigente y cierra el CREATED inicial")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideInTransitCases")
    void create_adoption_transitionsByInTransit(String testName, String body, String expectedStatus) throws Exception {
        String responseBody = mockMvc.perform(
                        post("/animal-posts")
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

    @DisplayName("POST /animal-post LOST: nace en SEARCHING (con dueno) o TO_RESCUE (en la calle) y cierra el CREATED")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideLostTransitionCases")
    void create_lost_transitionsToInitialStatus(String testName, String body, String expectedStatus) throws Exception {
        String responseBody = mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(mapper.readTree(responseBody).get("id").asText());
        LostPost saved = (LostPost) animalPostRepository.findById(id).orElseThrow();

        assertThat(saved.getStatusHistory())
                .extracting(h -> h.getStatus().name())
                .containsExactlyInAnyOrder("CREATED", expectedStatus);
        assertThat(saved.getStatusHistory())
                .filteredOn(h -> h.getStatus() == StatusLostPost.CREATED)
                .singleElement()
                .satisfies(h -> assertThat(h.getFinishedAt()).isNotNull());
        assertThat(saved.getCurrentStatus().getStatus().name()).isEqualTo(expectedStatus);
        assertThat(saved.getCurrentStatus().getFinishedAt()).isNull();
    }

    @Test
    @DisplayName("GET /animal-posts devuelve los casos del más nuevo al más viejo")
    void list_returnsNewestFirst() throws Exception {
        saveLostPost("Caso perdido", StatusLostPost.CREATED);
        Thread.sleep(2);
        saveAdoptionPost("Caso adopción", StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT);

        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Caso adopción"))
                .andExpect(jsonPath("$.content[1].name").value("Caso perdido"))
                .andExpect(jsonPath("$.content[0].status").value("SEARCHING_ADOPT_AND_TRANSIT"));
    }

    @DisplayName("GET /animal-posts filtra por type y/o status")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideFilterCases")
    void filterTests(String testName, String type, String status, int expectedCount) throws Exception {
        saveLostPost("Lost creado", StatusLostPost.CREATED);
        saveLostPost("Lost buscando", StatusLostPost.SEARCHING);
        saveLostPost("En la calle", StatusLostPost.TO_RESCUE, false);
        saveAdoptionPost("Adopción en búsqueda y tránsito", StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT);
        saveAdoptionPost("Adopción adoptada", StatusAdoptionPost.ADOPTED);

        MockHttpServletRequestBuilder request = get("/animal-posts")
                .header("Authorization", "Bearer " + accessToken);
        if (type != null) {
            request = request.param("type", type);
        }
        if (status != null) {
            request = request.param("status", status);
        }

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(expectedCount));
    }

    @Test
    @DisplayName("GET /animal-posts?status=SEARCHING devuelve solo perdidos en ese estado")
    void filterByStatus_returnsOnlyMatchingStatusAndSubtype() throws Exception {
        saveLostPost("En búsqueda", StatusLostPost.SEARCHING);
        saveLostPost("Recién creado", StatusLostPost.CREATED);
        saveAdoptionPost("Adopción", StatusAdoptionPost.SEARCHING_ADOPT);

        mockMvc.perform(
                        get("/animal-posts")
                                .param("status", "SEARCHING")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("En búsqueda"))
                .andExpect(jsonPath("$.content[0].type").value("LOST"))
                .andExpect(jsonPath("$.content[0].status").value("SEARCHING"));
    }

    @Test
    @DisplayName("GET /animal-posts respeta el tamaño de página y expone la metadata de paginación")
    void list_paginates() throws Exception {
        saveLostPost("Uno", StatusLostPost.CREATED);
        saveLostPost("Dos", StatusLostPost.CREATED);
        saveAdoptionPost("Tres", StatusAdoptionPost.SEARCHING_ADOPT_AND_TRANSIT);

        mockMvc.perform(
                        get("/animal-posts")
                                .param("page", "0")
                                .param("size", "2")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("GET /animal-posts sin publicaciones devuelve página vacía")
    void list_whenEmpty_returnsEmptyPage() throws Exception {
        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /animal-posts?type=INVALIDO devuelve 400")
    void list_withInvalidType_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        get("/animal-posts")
                                .param("type", "INVALIDO")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animal-posts sin token devuelve 401")
    void list_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/animal-posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /animal-posts/{id} del owner: 204, reemplaza todos los datos y preserva los no editables")
    void update_asOwner_fullReplaceUpdatesEverythingAndKeepsNonEditable() throws Exception {
        UUID postId = createOwnedPostReturningId();
        AnimalPost original = animalPostRepository.findById(postId).orElseThrow();
        UUID ownerId = original.getOwner().getId();
        UUID animalId = original.getAnimal().getId();
        UUID locationId = original.getLocation().getId();
        var createdAt = original.getCreatedAt();

        mockMvc.perform(
                        put("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.PUT_VALID)
                )
                .andExpect(status().isNoContent());

        LostPost updated = (LostPost) animalPostRepository.findById(postId).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Titulo actualizado");
        assertThat(updated.getDescription()).isEqualTo("Descripcion actualizada");
        assertThat(updated.getImageUrl()).isEqualTo("cf-image-put");
        assertThat(updated.getPhoneNumber()).isEqualTo("1199887766");
        assertThat(updated.getReward()).isEqualByComparingTo("7500");
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getAnimal().getType()).isEqualTo(AnimalType.CAT);
        assertThat(updated.getAnimal().getSize()).isEqualTo(AnimalSize.LARGE);
        assertThat(updated.getAnimal().getGender()).isEqualTo(AnimalGender.FEMALE);
        assertThat(updated.getAnimal().getAge()).isEqualTo(AnimalAge.SENIOR);
        assertThat(updated.getAnimal().getColor()).isEqualTo("negro");
        assertThat(updated.getLocation().getName()).isEqualTo("Refugio Nuevo");
        assertThat(updated.getLocation().getAddress()).isEqualTo("Nueva direccion 456");
        assertThat(updated.getLocation().getLatitude()).isEqualTo(-34.7);
        assertThat(updated.getOwner().getId()).isEqualTo(ownerId);
        assertThat(updated.getAnimal().getId()).isEqualTo(animalId);
        assertThat(updated.getLocation().getId()).isEqualTo(locationId);
        assertThat(updated.getCreatedAt()).isEqualTo(createdAt);
    }

    @DisplayName("PUT /animal-posts/{id} con payload inválido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUpdateInvalidCases")
    void update_invalidPayload_returnsBadRequest(String testName, String body) throws Exception {
        UUID postId = createOwnedPostReturningId();

        mockMvc.perform(
                        put("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /animal-posts/{id} de un usuario que no es el owner devuelve 403")
    void update_asNonOwner_returnsForbidden() throws Exception {
        UUID postId = saveLostPostOwnedByOtherUser().getId();

        mockMvc.perform(
                        put("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.PUT_VALID)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /animal-posts/{id} inexistente devuelve 404 con mensaje")
    void update_nonExistentPost_returnsNotFound() throws Exception {
        mockMvc.perform(
                        put("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.PUT_VALID)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("DELETE /animal-posts/{id} del owner: 204 y la publicación deja de existir")
    void delete_asOwner_removesPost() throws Exception {
        UUID postId = createOwnedPostReturningId();

        mockMvc.perform(
                        delete("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("DELETE /animal-posts/{id} de un usuario que no es el owner devuelve 403 y no elimina")
    void delete_asNonOwner_returnsForbiddenAndKeepsPost() throws Exception {
        UUID postId = saveLostPostOwnedByOtherUser().getId();

        mockMvc.perform(
                        delete("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());

        assertThat(animalPostRepository.findById(postId)).isPresent();
    }

    @Test
    @DisplayName("DELETE /animal-posts/{id} inexistente devuelve 404 con mensaje")
    void delete_nonExistentPost_returnsNotFound() throws Exception {
        mockMvc.perform(
                        delete("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("PUT /animal-posts/{id} sin token devuelve 401")
    void update_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/animal-posts/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.PUT_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /animal-posts/{id} sin token devuelve 401")
    void delete_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/animal-posts/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /animal-posts/{id} con token inválido devuelve 401")
    void update_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.PUT_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /animal-posts/{id} con token inválido devuelve 401")
    void delete_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        delete("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                )
                .andExpect(status().isUnauthorized());
    }

    @Sql("/sql/animal_posts/get-animal-post.sql")
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideExistingAnimalPosts")
    void getAnimalPost_existing_returnsData(
            String testName,
            String id,
            String expectedType,
            String expectedName
    ) throws Exception {

        mockMvc.perform(
                        get("/animal-posts/" + id)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.name").value(expectedName));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideGetAnimalPostCases")
    void getAnimalPost_securityCases(String testName, String id, String token, HttpStatus expectedStatus) throws Exception {

        MockHttpServletRequestBuilder request =
                get("/animal-posts/" + id);

        if ("VALID".equals(token)) {
            request = request.header("Authorization", "Bearer " + accessToken);
        } else if (token != null) {
            request = request.header("Authorization", token);
        }

        mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()));
    }


    @DisplayName("PATCH /animal-posts/{id}/status — transiciones validas, estado ajeno al tipo y transiciones prohibidas")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideStatusTransitionCases")
    void transitionStatusTests(
            String testName,
            AnimalPostFilter postType,
            String startStatus,
            String targetStatus,
            HttpStatus expectedStatus
    ) throws Exception {
        UUID postId = seedPost(postType, startStatus);

        var result = patchStatus(postId, targetStatus)
                .andExpect(status().is(expectedStatus.value()));

        if (expectedStatus.is2xxSuccessful()) {
            result.andExpect(jsonPath("$.status").value(targetStatus));
        }
    }

    @Test
    @DisplayName("PATCH /animal-posts/{id}/status persiste el nuevo estado y cierra el anterior")
    void transitionStatus_persistsNewStatusAndClosesPrevious() throws Exception {
        LostPost post = saveLostPost("Perdido", StatusLostPost.SEARCHING);

        patchStatus(post.getId(), "FOUND").andExpect(status().isOk());

        LostPost saved = (LostPost) animalPostRepository.findById(post.getId()).orElseThrow();
        assertThat(saved.getCurrentStatus().getStatus()).isEqualTo(StatusLostPost.FOUND);
        assertThat(saved.getStatusHistory())
                .filteredOn(history -> history.getStatus() == StatusLostPost.SEARCHING)
                .allSatisfy(history -> assertThat(history.getFinishedAt()).isNotNull());
    }

    @Test
    @DisplayName("PATCH /animal-posts/{id}/status de otro usuario devuelve 403")
    void transitionStatus_notOwner_returnsForbidden() throws Exception {
        LostPost post = saveLostPostOwnedByOtherUser();

        patchStatus(post.getId(), "FOUND").andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /animal-posts/{id}/status inexistente devuelve 404")
    void transitionStatus_nonExistentPost_returnsNotFound() throws Exception {
        patchStatus(UUID.randomUUID(), "FOUND").andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /animal-posts/{id}/status sin la clave status devuelve 400")
    void transitionStatus_withoutStatus_returnsBadRequest() throws Exception {
        LostPost post = saveLostPost("Perdido", StatusLostPost.SEARCHING);

        mockMvc.perform(
                        patch("/animal-posts/" + post.getId() + "/status")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ }")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem(containsString("estado es obligatorio"))));
    }

    @DisplayName("PATCH /animal-posts/{id}/status sin autenticacion valida devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideTransitionUnauthorizedCases")
    void transitionStatus_unauthorized(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = patch("/animal-posts/" + UUID.randomUUID() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new TransitionStatusRequest("FOUND")));

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    private UUID seedPost(AnimalPostFilter postType, String startStatus) {
        return switch (postType) {
            case LOST -> saveLostPost("Perdido", StatusLostPost.valueOf(startStatus), true).getId();
            case IN_STREET -> saveLostPost("En la calle", StatusLostPost.valueOf(startStatus), false).getId();
            case ADOPTION -> saveAdoptionPost("Adopcion", StatusAdoptionPost.valueOf(startStatus)).getId();
        };
    }

    private ResultActions patchStatus(UUID postId, String targetStatus) throws Exception {
        return mockMvc.perform(
                patch("/animal-posts/" + postId + "/status")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new TransitionStatusRequest(targetStatus)))
        );
    }

    private UUID createOwnedPostReturningId() throws Exception {
        String responseBody = mockMvc.perform(
                        post("/animal-posts")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(mapper.readTree(responseBody).get("id").asText());
    }

    private LostPost saveLostPostOwnedByOtherUser() {
        User other = new User("otro-usuario", "x", new Profile("otro@mail.com", "111", List.of(Rol.COMMUNITY)));
        userRepository.save(other);

        LostPost post = new LostPost("De otro", "Descripcion", "cf-img", null, "111", true, other, location(), animal(), null);
        return animalPostRepository.save(post);
    }

    private LostPost saveLostPost(String name, StatusLostPost status) {
        return saveLostPost(name, status, true);
    }

    private LostPost saveLostPost(String name, StatusLostPost status, boolean hasOwner) {
        LostPost post = new LostPost(name, "Descripción", "cf-img", null, null, hasOwner, admin(), location(), animal(), null);
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        return animalPostRepository.save(post);
    }

    private AdoptionPost saveAdoptionPost(String name, StatusAdoptionPost status) {
        AdoptionPost post = new AdoptionPost(name, "Descripción", "cf-img", null, null, admin(), animal(), location(), false);
        post.setStatusHistory(new ArrayList<>(List.of(new AdoptionPostStatusHistory(status, post))));
        return animalPostRepository.save(post);
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private Animal animal() {
        Animal animal = new Animal();
        animal.setType(AnimalType.DOG);
        animal.setSize(AnimalSize.MEDIUM);
        animal.setGender(AnimalGender.MALE);
        return animal;
    }

    private Location location() {
        return new Location("Parque", "Av. Patricias", 100, -34.6, -58.4);
    }
}
