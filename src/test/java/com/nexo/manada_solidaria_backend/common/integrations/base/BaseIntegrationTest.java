package com.nexo.manada_solidaria_backend.common.integrations.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Slf4j
@SpringBootTest
public abstract class BaseIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    protected ObjectMapper mapper;

    @BeforeAll
    public static void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    protected String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error decoding object as String", e);
            return null;
        }
    }

    protected static String getCredentials(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    protected String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error decoding object as String", e);
            return null;
        }
    }
}
