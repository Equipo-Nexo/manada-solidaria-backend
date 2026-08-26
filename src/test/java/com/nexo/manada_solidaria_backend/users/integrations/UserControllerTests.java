package com.nexo.manada_solidaria_backend.users.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.common.data.models.PhoneNumber;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTests extends BaseAuthenticatedIntegrationTest {

    private static final String MOCK_DATA =
            "com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils#";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnimalPostRepository animalPostRepository;

    @DisplayName("GET /users devuelve perfil, publicaciones y metricas")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUserDetailFieldCases")
    @Sql(
            scripts = {
                    "/sql/users/user-profile-data.sql",
                    "/sql/users/create-campaigns.sql",
                    "/sql/users/create-animal-posts.sql",
                    "/sql/users/create-fundraising.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getUser_returnsFullDetail(
            String testName,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        getUser(adminId())
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @DisplayName("GET /users/{userId} devuelve el usuario del path, no el del token")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUserResolutionCases")
    void getUser_resolvesTarget(
            String testName,
            boolean otherUser,
            String expectedUsername
    ) throws Exception {
        User other = saveUser("otro");

        getUser(otherUser ? other.getId() : adminId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(expectedUsername));
    }

    @DisplayName("GET /users/{userId}/profile devuelve el perfil sin las publicaciones")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUserProfileCases")
    @Sql(
            scripts = {
                    "/sql/users/user-profile-data.sql",
                    "/sql/users/create-campaigns.sql",
                    "/sql/users/create-animal-posts.sql",
                    "/sql/users/create-fundraising.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getUserProfile_returnsProfileWithoutPosts(
            String testName,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        getUserProfile(adminId())
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("GET /users/{userId}/profile no incluye el campo posts")
    void getUserProfile_omitsPostsField() throws Exception {
        getUserProfile(adminId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").doesNotExist());
    }

    @DisplayName("Pedir un usuario inexistente devuelve 404")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideNotFoundCases")
    void getUser_whenUserDoesNotExist_returnsNotFound(String testName, String pathTemplate) throws Exception {
        mockMvc.perform(
                        get(pathTemplate.formatted(UUID.randomUUID()))
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("GET del detalle de usuario sin autenticacion valida devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUnauthorizedPathCases")
    void getUser_unauthorized(String testName, String path, String token) throws Exception {
        MockHttpServletRequestBuilder request = get(path);
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @DisplayName("GET /users filtra por nombre de usuario y/o rol")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideGetUsersFilterCases")
    void getUsers_filters(String testName, String username, String role, List<String> expectedUsernames) throws Exception {
        saveUser("rescatista", new PhoneNumber("3533", "436249"), "cf-rescatista", Rol.RESCUER);
        saveUser("refugio", new PhoneNumber("3533", "436250"), "cf-refugio",
                Rol.RESCUER, Rol.TRANSITIONAL_HOME);

        MockHttpServletRequestBuilder request = get("/users")
                .header("Authorization", "Bearer " + accessToken);
        if (username != null) {
            request = request.param("username", username);
        }
        if (role != null) {
            request = request.param("role", role);
        }

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedUsernames.size())))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder(expectedUsernames.toArray())));
    }

    @DisplayName("GET /users mapea el username del User y roles/telefono/foto del Profile")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUserFieldCases")
    void getUsers_mapsUserAndProfileFields(
            String testName,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        saveUser("rescatista", new PhoneNumber("3533", "436249"), "cf-rescatista", Rol.RESCUER);

        mockMvc.perform(
                        get("/users")
                                .param("username", "rescatista")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("GET /users?role=INVALIDO devuelve 400")
    void getUsers_withInvalidRole_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        get("/users")
                                .param("role", "INVALIDO")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /users sin autenticacion valida devuelve 401")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUnauthorizedTokenCases")
    void getUsers_unauthorized(String testName, String token) throws Exception {
        MockHttpServletRequestBuilder request = get("/users");
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @DisplayName("Get user posts")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils#provideGetUserPostsTestCases")
    @Sql(
            scripts = {
                    "/sql/users/create-campaigns.sql",
                    "/sql/users/create-animal-posts.sql",
                    "/sql/users/create-fundraising.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getUserPosts(
            String testName,
            String typeQueryParam,
            int expectedResponseSize
    ) throws Exception {

        mockMvc.perform(
                        get("/users/posts")
                                .queryParam("type", typeQueryParam)
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResponseSize)));
    }

    @Test
    @DisplayName("Authenticated user is not the owner of the unique post, list should be empty")
    @Sql(
            scripts = {
                    "/sql/users/post-that-owner-is-not-admin.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getUserPostsWithoutOwnership() throws Exception {

        mockMvc.perform(
                        get("/users/posts")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DisplayName("PUT /users/profile refleja en la response los datos enviados")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUpdateProfileResponseCases")
    void updateProfile_returnsUpdatedProfile(
            String testName,
            String body,
            String jsonPathExpression,
            Matcher<?> expected
    ) throws Exception {
        putProfile(body)
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathExpression, expected));
    }

    @Test
    @DisplayName("PUT /users/profile persiste los cambios del usuario autenticado")
    void updateProfile_persistsChanges() throws Exception {
        putProfile(MockUserDataUtils.UPDATE_PROFILE_VALID).andExpect(status().isOk());

        Profile saved = userRepository.findByUsername("admin").orElseThrow().getProfile();
        assertThat(saved.getName()).isEqualTo("Elian");
        assertThat(saved.getEmail()).isEqualTo("nuevo@mail.com");
        assertThat(saved.getProfileImageURL()).isEqualTo("cf-profile-1");
    }

    @DisplayName("PUT /users/profile con payload invalido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUpdateProfileInvalidCases")
    void updateProfile_invalidPayload_returnsBadRequest(String testName, String body) throws Exception {
        putProfile(body).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /users/profile sin token devuelve 401")
    void updateProfile_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockUserDataUtils.UPDATE_PROFILE_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /users/profile con token invalido devuelve 401")
    void updateProfile_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        put("/users/profile")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockUserDataUtils.UPDATE_PROFILE_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("PATCH /users/roles — COMMUNITY se agrega solo si no viene RESCUER")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideUpdateRolesCases")
    void updateRolesTests(String testName, UpdateRolesRequest request, List<Rol> expectedRoles) throws Exception {
        patchRoles(toJson(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder(expectedRoles.stream().map(Rol::name).toArray())));

        assertThat(rolesOfAdmin()).containsExactlyInAnyOrderElementsOf(expectedRoles);
    }

    @DisplayName("PATCH /users/roles con un rol no editable (VET/COMMUNITY) devuelve 400 con los valores permitidos")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource(MOCK_DATA + "provideNonEditableRoleCases")
    void updateRoles_nonEditableRole_returnsBadRequest(String testName, String body) throws Exception {
        patchRoles(body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", containsString("RESCUER")));
    }

    @Test
    @DisplayName("PATCH /users/roles sin la clave roles devuelve 400")
    void updateRoles_missingRolesKey_returnsBadRequest() throws Exception {
        patchRoles(MockUserDataUtils.ROLES_MISSING_KEY).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/roles sin token devuelve 401")
    void updateRoles_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        patch("/users/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockUserDataUtils.ROLES_WITH_RESCUER))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /users/roles con token invalido devuelve 401")
    void updateRoles_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        patch("/users/roles")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(MockUserDataUtils.ROLES_WITH_RESCUER))
                )
                .andExpect(status().isUnauthorized());
    }

    private ResultActions putProfile(String body) throws Exception {
        return mockMvc.perform(
                put("/users/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }

    private ResultActions patchRoles(String body) throws Exception {
        return mockMvc.perform(
                patch("/users/roles")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }

    private ResultActions getUser(UUID userId) throws Exception {
        return mockMvc.perform(
                get("/users/" + userId).header("Authorization", "Bearer " + accessToken)
        );
    }

    private ResultActions getUserProfile(UUID userId) throws Exception {
        return mockMvc.perform(
                get("/users/" + userId + "/profile").header("Authorization", "Bearer " + accessToken)
        );
    }

    private User saveUser(String username) {
        return userRepository.save(
                new User(username, "irrelevante",
                        new Profile("otro@mail.com", new PhoneNumber("3533", "436249"), List.of(Rol.COMMUNITY)))
        );
    }

    private UUID adminId() {
        return userRepository.findByUsername("admin").orElseThrow().getId();
    }

    private void saveLostPost(User owner, StatusLostPost status) {
        LostPost post = new LostPost(
                "Publicacion de prueba", "Descripcion", "cf-img", null, null, true,
                owner, new Location("Parque", "Av. Patricias", 100, -34.6, -58.4), new Animal(), null
        );
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        animalPostRepository.save(post);
    }

    private void saveUser(String username, PhoneNumber phoneNumber, String profileImageURL, Rol... roles) {
        Profile profile = new Profile(null, phoneNumber, new ArrayList<>(List.of(roles)));
        profile.setProfileImageURL(profileImageURL);
        userRepository.save(new User(username, "x", profile));
    }

    private List<Rol> rolesOfAdmin() {
        return userRepository.findByUsername("admin").orElseThrow().getProfile().getRoles();
    }
}
