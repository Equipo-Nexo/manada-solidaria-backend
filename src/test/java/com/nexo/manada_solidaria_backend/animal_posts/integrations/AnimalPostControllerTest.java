package com.nexo.manada_solidaria_backend.animal_posts.integrations;

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
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()));

        // Y realmente quedó en la BBDD: se recupera por el GET con los mismos datos y dueño.
        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.content[0].ownerId").value(adminId.toString()))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
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
        saveLostPost("En la calle", StatusLostPost.SEARCHING, false);
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

    @Test
    @DisplayName("GET /animal-posts/{id} devuelve la publicación")
    void getAnimalPost_returnsAnimalPost() throws Exception {
        UUID postId = createOwnedPostReturningId();

        mockMvc.perform(
                        get("/animal-posts/" + postId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.type").value("LOST"))
                .andExpect(jsonPath("$.name").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.description").value("Se escapó en el parque"))
                .andExpect(jsonPath("$.imageUrl").value("cf-image-123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.animal.type").value("DOG"))
                .andExpect(jsonPath("$.location.name").value("Parque Centenario"))
                .andExpect(jsonPath("$.phoneNumber").value("1122334455"))
                .andExpect(jsonPath("$.reward").value(5000));
    }

    @Test
    @DisplayName("GET /animal-posts/{id} inexistente devuelve 404")
    void getAnimalPost_nonExistent_returnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasItem(containsString("no existe"))));
    }

    @Test
    @DisplayName("GET /animal-posts/{id} sin token devuelve 401")
    void getAnimalPost_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/animal-posts/" + UUID.randomUUID())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /animal-posts/{id} con token inválido devuelve 401")
    void getAnimalPost_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/animal-posts/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                )
                .andExpect(status().isUnauthorized());
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

    private void saveLostPost(String name, StatusLostPost status) {
        saveLostPost(name, status, true);
    }

    private void saveLostPost(String name, StatusLostPost status, boolean hasOwner) {
        LostPost post = new LostPost(name, "Descripción", "cf-img", null, null, hasOwner, null, location(), animal(), null);
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        animalPostRepository.save(post);
    }

    private void saveAdoptionPost(String name, StatusAdoptionPost status) {
        AdoptionPost post = new AdoptionPost(name, "Descripción", "cf-img", null, null, null, animal(), location(), false);
        post.setStatusHistory(new ArrayList<>(List.of(new AdoptionPostStatusHistory(status, post))));
        animalPostRepository.save(post);
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
