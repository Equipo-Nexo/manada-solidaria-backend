package com.nexo.manada_solidaria_backend.users.integrations;

import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTests extends BaseAuthenticatedIntegrationTest {

    @Test
    void getUserPosts() throws Exception {
        mockMvc.perform(
                        get("/users/posts")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk());
    }
    
}
