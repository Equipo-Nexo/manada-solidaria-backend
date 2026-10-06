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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.FORBIDDEN_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /adoption-forms válido: persiste el formulario con descripción, sus respuestas y asocia el usuario autenticado")
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
                .andExpect(jsonPath("$.description").value("Quiero adoptar una gatita para que crezca con mi gato."))
                .andExpect(jsonPath("$.phoneNumber.areaCode").value("353"))
                .andExpect(jsonPath("$.phoneNumber.number").value("4123456"))
                .andExpect(jsonPath("$.isRead").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.answers", hasSize(2)))
                .andExpect(jsonPath("$.answers[0].questionId").value(q1.getId().toString()))
                .andExpect(jsonPath("$.answers[0].questionTitle").value("¿Alquilás? ¿Te permiten mascotas?"))
                .andExpect(jsonPath("$.answers[0].answer").value("Alquilo y sí me permiten."));

        List<AdoptionForm> savedForms = adoptionFormRepository.findAll();
        assertThat(savedForms).hasSize(1);

        AdoptionForm saved = savedForms.get(0);
        assertThat(saved.getAdoptionPost().getId()).isEqualTo(post.getId());
        assertThat(saved.getApplicant().getId()).isEqualTo(authenticatedUser.getId());
        assertThat(saved.getDescription()).isEqualTo("Quiero adoptar una gatita para que crezca con mi gato.");
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
                "Descripción opcional corta",
                new PhoneNumberRequest("353", "4123456"),
                List.of(
                        new CreateAdoptionFormRequest.AnswerFormRequest(q1.getId(), ""),
                        new CreateAdoptionFormRequest.AnswerFormRequest(q2.getId(), "No")
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
                .andExpect(jsonPath("$.description").value("Descripción opcional corta"))
                .andExpect(jsonPath("$.answers", hasSize(2)))
                .andExpect(jsonPath("$.answers[0].answer").value(""));

        List<AdoptionForm> savedForms = adoptionFormRepository.findAll();
        assertThat(savedForms).hasSize(1);
        assertThat(savedForms.get(0).getAnswers().get(0).getAnswer()).isEmpty();
    }

    @Test
    @DisplayName("GET /adoption-forms/{adoptionFormId} — Obtiene el detalle del formulario para el postulante")
    void getFormById_returnsOk() throws Exception {
        User owner = createOtherUser("owner-get-id", "owner_get_id@mail.com");
        User applicant = createOtherUser("applicant-get-id", "applicant_get_id@mail.com");

        AdoptionPost post = saveAdoptionPost("Busco hogar para gata", owner);
        QuestionForm q1 = saveQuestion("¿Alquilás? ¿Te permiten mascotas?");
        QuestionForm q2 = saveQuestion("¿Contás con patio cerrado?");

        AdoptionForm form = new AdoptionForm(
                "Quiero una gatita para que le haga compañía a mi gato de 2 años para que crezcan juntos.",
                new PhoneNumber("353", "4123456"),
                applicant,
                post
        );
        form.addAnswer(new AdoptionFormDetail("Alquilo y sí me permiten.", form, q1));
        form.addAnswer(new AdoptionFormDetail("Sí, totalmente cerrado.", form, q2));
        AdoptionForm savedForm = adoptionFormRepository.save(form);

        String applicantToken = createTokenForUser(applicant);

        mockMvc.perform(
                        get("/adoption-forms/{adoptionFormId}", savedForm.getId())
                                .header("Authorization", "Bearer " + applicantToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedForm.getId().toString()))
                .andExpect(jsonPath("$.adoptionPostId").value(post.getId().toString()))
                .andExpect(jsonPath("$.animalName").value("Busco hogar para gata"))
                .andExpect(jsonPath("$.applicantId").value(applicant.getId().toString()))
                .andExpect(jsonPath("$.description").value("Quiero una gatita para que le haga compañía a mi gato de 2 años para que crezcan juntos."))
                .andExpect(jsonPath("$.phoneNumber.areaCode").value("353"))
                .andExpect(jsonPath("$.phoneNumber.number").value("4123456"))
                .andExpect(jsonPath("$.categories").isArray());
    }

    @Test
    @DisplayName("GET /adoption-forms/{adoptionFormId} cuando un usuario sin relación intenta acceder devuelve FORBIDDEN 403")
    void getFormById_unauthorizedUser_returnsForbidden() throws Exception {
        User owner = createOtherUser("owner-forb", "owner_forb@mail.com");
        User applicant = createOtherUser("applicant-forb", "applicant_forb@mail.com");
        AdoptionPost post = saveAdoptionPost("Mascota en adopción", owner);

        AdoptionForm form = new AdoptionForm(
                "Descripción de prueba",
                new PhoneNumber("353", "4123456"),
                applicant,
                post
        );
        AdoptionForm savedForm = adoptionFormRepository.save(form);

        mockMvc.perform(
                        get("/adoption-forms/{adoptionFormId}", savedForm.getId())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors", hasItem(containsString("No tienes permisos para consultar este formulario de adopción"))));
    }

    @Test
    @DisplayName("GET /adoption-forms/{adoptionFormId} con ID inexistente devuelve NOT_FOUND 404")
    void getFormById_nonExistent_returnsNotFound() throws Exception {
        mockMvc.perform(
                        get("/adoption-forms/{adoptionFormId}", UUID.randomUUID())
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET /adoption-forms/{adoptionFormId} sin autenticación o con token inválido devuelve UNAUTHORIZED 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetFormsUnauthorizedCases")
    void getFormById_unauthorizedCases(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/adoption-forms/{adoptionFormId}", MockAdoptionFormDataUtils.FORM_ID);

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

    private String createTokenForUser(User user) throws Exception {
        String response = mockMvc.perform(
                post("/auth/login")
                        .header("Authorization", getCredentials(user.getUsername(), "password"))
        ).andReturn().getResponse().getContentAsString();

        return mapper.readTree(response).get("accessToken").asText();
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
        User user = new User(
                username,
                passwordEncoder.encode("password"),
                new Profile(email, new PhoneNumber("353", "999999"), Set.of(Rol.COMMUNITY))
        );
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
                "Descripción del solicitante",
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
                "Descripción del solicitante",
                new PhoneNumber("353", "4123456"),
                applicant,
                adminPost
        );
        form.addAnswer(new AdoptionFormDetail("Sí", form, q));
        adoptionFormRepository.save(form);
    }
}