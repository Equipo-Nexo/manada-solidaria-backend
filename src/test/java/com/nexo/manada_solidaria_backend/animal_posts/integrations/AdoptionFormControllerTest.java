package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAdoptionFormDataUtils;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AdoptionFormControllerTest extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.animal_posts.utils.MockAdoptionFormDataUtils#";

    @Autowired
    private AdoptionFormRepository adoptionFormRepository;

    @Autowired
    private AnimalPostRepository animalPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /adoption-forms válido: persiste el formulario, sus preguntas y asocia el usuario autenticado")
    void createForm_persistsFormAndReturnsCreated() throws Exception {
        User postOwner = createOtherUser("owner-user", "owner@mail.com");
        AdoptionPost post = saveAdoptionPost("Gatito en adopción", postOwner);
        User authenticatedUser = admin();

        CreateAdoptionFormRequest requestDto = MockAdoptionFormDataUtils.createValidRequest(post.getId());
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.adoptionPostId").value(post.getId().toString()))
                .andExpect(jsonPath("$.applicantId").value(authenticatedUser.getId().toString()))
                .andExpect(jsonPath("$.phoneNumber.areaCode").value("353"))
                .andExpect(jsonPath("$.phoneNumber.number").value("4123456"))
                .andExpect(jsonPath("$.isRead").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.questions", hasSize(2)))
                .andExpect(jsonPath("$.questions[0].question").value("¿Alquilás? ¿Te permiten mascotas?"))
                .andExpect(jsonPath("$.questions[0].answer").value("Alquilo y sí me permiten."));

        // Verificación de persistencia real en BBDD
        List<AdoptionForm> savedForms = adoptionFormRepository.findAll();
        assertThat(savedForms).hasSize(1);

        AdoptionForm saved = savedForms.get(0);
        assertThat(saved.getAdoptionPost().getId()).isEqualTo(post.getId());
        assertThat(saved.getApplicant().getId()).isEqualTo(authenticatedUser.getId());
        assertThat(saved.isRead()).isFalse();
        assertThat(saved.getQuestions()).hasSize(2);
        assertThat(saved.getQuestions().get(0).getQuestion()).isEqualTo("¿Alquilás? ¿Te permiten mascotas?");
    }

    @Test
    @DisplayName("POST /adoption-forms del propio dueño de la publicación devuelve BAD_REQUEST 400")
    void createForm_whenApplicantIsOwner_returnsBadRequest() throws Exception {
        User ownerAndApplicant = admin(); // El usuario autenticado mediante el accessToken
        AdoptionPost post = saveAdoptionPost("Mi perro en adopción", ownerAndApplicant);

        CreateAdoptionFormRequest requestDto = MockAdoptionFormDataUtils.createValidRequest(post.getId());
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /adoption-forms para una publicación inexistente devuelve NOT_FOUND 404")
    void createForm_nonExistentPost_returnsNotFound() throws Exception {
        CreateAdoptionFormRequest requestDto = MockAdoptionFormDataUtils.createValidRequest(UUID.randomUUID());
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("POST /adoption-forms — validación de campos obligatorios en la request")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateFormValidationCases")
    void createForm_validationCases(String testName, Object requestObj, HttpStatus expectedStatus) throws Exception {
        String body = objectMapper.writeValueAsString(requestObj);

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().is(expectedStatus.value()));
    }

    @DisplayName("POST /adoption-forms sin autenticación o con token inválido devuelve UNAUTHORIZED 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateFormUnauthorizedCases")
    void createForm_unauthorizedCases(String testName, String token) throws Exception {
        CreateAdoptionFormRequest requestDto = MockAdoptionFormDataUtils.createValidRequest(UUID.randomUUID());
        String body = objectMapper.writeValueAsString(requestDto);

        MockHttpServletRequestBuilder request = post("/adoption-forms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /adoption-forms/post/{postId} — El dueño de la publicación obtiene sus formularios exitosamente")
    void getFormsByPostId_asOwner_returnsOk() throws Exception {
        User owner = admin();
        AdoptionPost post = saveAdoptionPost("Mi gato en adopcion", owner);

        mockMvc.perform(
                        get("/adoption-forms/post/{postId}", post.getId())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("GET /adoption-forms/post/{postId} — Casos de error (404 Not Found)")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetFormsByPostIdErrorCases")
    void getFormsByPostId_errorCases(String testName, UUID postId, HttpStatus expectedStatus) throws Exception {
        mockMvc.perform(
                        get("/adoption-forms/post/{postId}", postId)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().is(expectedStatus.value()));
    }

    @Test
    @DisplayName("GET /adoption-forms/post/{postId} cuando el usuario autenticado NO es el dueño devuelve FORBIDDEN 403")
    void getFormsByPostId_whenUserIsNotOwner_returnsForbidden() throws Exception {
        User postOwner = createOtherUser("other-post-owner", "otherowner@mail.com");
        AdoptionPost postOfOtherUser = saveAdoptionPost("Mascota de otro dueño", postOwner);

        mockMvc.perform(
                        get("/adoption-forms/post/{postId}", postOfOtherUser.getId())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("GET /adoption-forms/post/{postId} sin autenticación o con token inválido devuelve UNAUTHORIZED 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetFormsUnauthorizedCases")
    void getFormsByPostId_unauthorizedCases(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/adoption-forms/post/{postId}", MockAdoptionFormDataUtils.POST_WITH_FORMS_ID);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    private AdoptionPost saveAdoptionPost(String name, User owner) {
        AdoptionPost post = new AdoptionPost(
                name,
                "Descripción de prueba",
                "cf-img-id",
                null,
                new PhoneNumber("353", "123456"),
                owner,
                null,
                new Location("Córdoba", "Av. Siempre Viva", 123, -31.4, -64.1),
                false
        );
        post.getStatusHistory().add(new AdoptionPostStatusHistory(StatusAdoptionPost.SEARCHING_ADOPT, post));
        return animalPostRepository.save(post);
    }

    private User createOtherUser(String username, String email) {
        User user = new User(username, "password", new Profile(email, new PhoneNumber("353", "999999"), List.of(Rol.COMMUNITY)));
        return userRepository.save(user);
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

}