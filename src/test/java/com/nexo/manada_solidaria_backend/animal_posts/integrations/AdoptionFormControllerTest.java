package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.CreateAdoptionFormRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.QuestionType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionFormDetail;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.QuestionForm;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.QuestionFormRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAdoptionFormDataUtils;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.FORBIDDEN_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private QuestionFormRepository questionFormRepository;

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

        QuestionForm q1 = saveQuestion("¿Alquilás? ¿Te permiten mascotas?");
        QuestionForm q2 = saveQuestion("¿Contás con patio cerrado?");

        CreateAdoptionFormRequest request = MockAdoptionFormDataUtils.createValidRequest(
                post.getId(),
                q1.getId(),
                q2.getId()
        );

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
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
                .andExpect(jsonPath("$.questions[0].questionTitle").value("¿Alquilás? ¿Te permiten mascotas?"))
                .andExpect(jsonPath("$.questions[0].answer").value("Alquilo y sí me permiten."));

        List<AdoptionForm> savedForms = adoptionFormRepository.findAll();
        assertThat(savedForms).hasSize(1);

        AdoptionForm saved = savedForms.get(0);
        assertThat(saved.getAdoptionPost().getId()).isEqualTo(post.getId());
        assertThat(saved.getApplicant().getId()).isEqualTo(authenticatedUser.getId());
        assertThat(saved.isRead()).isFalse();
        assertThat(saved.getAnswers()).hasSize(2);
        assertThat(saved.getAnswers().get(0).getQuestionForm().getTitle()).isEqualTo("¿Alquilás? ¿Te permiten mascotas?");
    }

    @Test
    @DisplayName("POST /adoption-forms del propio dueño de la publicación devuelve BAD_REQUEST 400")
    void createForm_whenApplicantIsOwner_returnsBadRequest() throws Exception {
        User ownerAndApplicant = admin();
        AdoptionPost post = saveAdoptionPost("Mi perro en adopción", ownerAndApplicant);
        QuestionForm q1 = saveQuestion("¿Tenés patio?");
        QuestionForm q2 = saveQuestion("¿Experiencia previa?");

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockAdoptionFormDataUtils.createValidRequest(post.getId(), q1.getId(), q2.getId())))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /adoption-forms para una publicación inexistente devuelve NOT_FOUND 404")
    void createForm_nonExistentPost_returnsNotFound() throws Exception {
        QuestionForm q1 = saveQuestion("¿Tenés patio?");
        QuestionForm q2 = saveQuestion("¿Experiencia previa?");

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockAdoptionFormDataUtils.createValidRequest(UUID.randomUUID(), q1.getId(), q2.getId())))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("POST /adoption-forms — validación de campos obligatorios en la request")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateFormValidationCases")
    void createForm_validationCases(String testName, Object requestObj, HttpStatus expectedStatus) throws Exception {
        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(requestObj))
                )
                .andExpect(status().is(expectedStatus.value()));
    }

    @DisplayName("POST /adoption-forms sin autenticación o con token inválido devuelve UNAUTHORIZED 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideCreateFormUnauthorizedCases")
    void createForm_unauthorizedCases(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = post("/adoption-forms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockAdoptionFormDataUtils.createValidRequest(UUID.randomUUID())));

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /adoption-forms con respuesta en blanco/opcional: persiste el formulario correctamente y devuelve 201 CREATED")
    void createForm_withBlankAnswer_persistsFormAndReturnsCreated() throws Exception {
        User postOwner = createOtherUser("owner-user", "owner@mail.com");
        AdoptionPost post = saveAdoptionPost("Gatito en adopción", postOwner);

        QuestionForm q1 = saveQuestion("¿Tenés patio?");
        QuestionForm q2 = saveQuestion("¿Alquilás?");

        CreateAdoptionFormRequest request = new CreateAdoptionFormRequest(
                post.getId(),
                new PhoneNumberRequest("353", "4123456"),
                List.of(
                        new CreateAdoptionFormRequest.QuestionFormRequest(q1.getId(), ""),
                        new CreateAdoptionFormRequest.QuestionFormRequest(q2.getId(), "No")
                )
        );

        mockMvc.perform(
                        post("/adoption-forms")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.questions", hasSize(2)))
                .andExpect(jsonPath("$.questions[0].answer").value(""));

        List<AdoptionForm> savedForms = adoptionFormRepository.findAll();
        assertThat(savedForms).hasSize(1);
        assertThat(savedForms.get(0).getAnswers().get(0).getAnswer()).isEmpty();
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
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", hasItem(containsString(FORBIDDEN_MESSAGE))));
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

    @DisplayName("GET /users/adoption-forms — Consulta exitosa parametrizada por filtro (OWNER y REVIEWER)")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetFormsByUserFilterCases")
    void getFormsByUser_successCases(String testName, String filter, int expectedSize) throws Exception {
        adoptionFormRepository.deleteAll();

        User currentAdmin = admin();
        User otherUser = createOtherUser("other-user-filter-" + UUID.randomUUID(), "otherfilter@mail.com");

        setupMockFormsForFilter(filter, expectedSize, currentAdmin, otherUser);

        mockMvc.perform(
                        get("/users/adoption-forms")
                                .param("filter", filter)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(expectedSize)));
    }

    @DisplayName("GET /users/adoption-forms — Sin autenticación o con token inválido devuelve UNAUTHORIZED 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetFormsByUserUnauthorizedCases")
    void getFormsByUser_unauthorizedCases(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/users/adoption-forms")
                .param("filter", "OWNER");

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

    private QuestionForm saveQuestion(String title) {
        QuestionForm q = new QuestionForm(
                title,
                QuestionType.TEXT,
                "icon",
                "placeholder",
                1,
                true,
                null,
                new ArrayList<>(),
                UUID.randomUUID()
        );
        return questionFormRepository.save(q);
    }

    private User createOtherUser(String username, String email) {
        User user = new User(username, "password", new Profile(email, new PhoneNumber("353", "999999"), Set.of(Rol.COMMUNITY)));
        return userRepository.save(user);
    }

    private User admin() {
        return userRepository.findByUsername("admin").orElseThrow();
    }

    private void setupMockFormsForFilter(String filter, int expectedSize, User currentAdmin, User otherUser) {
        if ("OWNER".equals(filter)) {
            createFormAsOwner(currentAdmin, otherUser);
        } else if ("REVIEWER".equals(filter) && expectedSize > 0) {
            createFormAsReviewer(currentAdmin, otherUser);
        }
    }

    private void createFormAsOwner(User applicant, User postOwner) {
        AdoptionPost postOfOther = saveAdoptionPost("Mascota de otro", postOwner);
        QuestionForm q = saveQuestion("¿Patio?");
        AdoptionForm form = new AdoptionForm(
                new PhoneNumber("353", "4123456"),
                applicant,
                postOfOther
        );
        form.addAnswer(new AdoptionFormDetail("Sí", form, q));
        adoptionFormRepository.save(form);
    }

    private void createFormAsReviewer(User postOwner, User applicant) {
        AdoptionPost adminPost = saveAdoptionPost("Mascota de admin", postOwner);
        QuestionForm q = saveQuestion("¿Patio?");
        AdoptionForm form = new AdoptionForm(
                new PhoneNumber("353", "4123456"),
                applicant,
                adminPost
        );
        form.addAnswer(new AdoptionFormDetail("Sí", form, q));
        adoptionFormRepository.save(form);
    }
}