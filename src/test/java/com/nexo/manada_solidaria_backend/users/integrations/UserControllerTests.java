package com.nexo.manada_solidaria_backend.users.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTests extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Get user posts")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils#provideGetUserPostsTestCases")
    @Sql(
            scripts = {
                    "/sql/users/create-campaigns.sql",
                    "/sql/users/create-animal-posts.sql"
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

    @Test
    @DisplayName("PUT /users/profile actualiza el perfil del usuario autenticado y lo persiste")
    void updateProfile_updatesAuthenticatedUserProfile() throws Exception {
        putProfile(MockUserDataUtils.UPDATE_PROFILE_VALID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Elian"))
                .andExpect(jsonPath("$.lastname").value("Enria"))
                .andExpect(jsonPath("$.email").value("nuevo@mail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1133334444"))
                .andExpect(jsonPath("$.profileImageURL").value("cf-profile-1"));

        Profile saved = userRepository.findByUsername("admin").orElseThrow().getProfile();
        assertThat(saved.getName()).isEqualTo("Elian");
        assertThat(saved.getEmail()).isEqualTo("nuevo@mail.com");
        assertThat(saved.getProfileImageURL()).isEqualTo("cf-profile-1");
    }

    @Test
    @DisplayName("PUT /users/profile es reemplazo total: lo que se omite queda en null")
    void updateProfile_isFullReplacement() throws Exception {
        putProfile(MockUserDataUtils.UPDATE_PROFILE_VALID).andExpect(status().isOk());

        putProfile(MockUserDataUtils.UPDATE_PROFILE_WITHOUT_IMAGE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageURL").value(nullValue()));

        assertThat(userRepository.findByUsername("admin").orElseThrow()
                .getProfile().getProfileImageURL()).isNull();
    }

    @DisplayName("PUT /users/profile con payload invalido devuelve 400")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.users.utils.MockUserDataUtils#provideUpdateProfileInvalidCases")
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

    private ResultActions putProfile(String body) throws Exception {
        return mockMvc.perform(
                put("/users/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }
}
