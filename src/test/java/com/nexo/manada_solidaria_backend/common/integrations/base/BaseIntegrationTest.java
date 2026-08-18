package com.nexo.manada_solidaria_backend.common.integrations.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Slf4j
@SpringBootTest
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(
        scripts = "/sql/data-setup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Transactional
public abstract class BaseIntegrationTest {
    private static final int MOCK_WEB_SERVER_PORT = 18080;
    protected static final MapDispatcher MOCK_SERVER_DISPATCHER = new MapDispatcher();
    protected static final MockWebServer MOCK_WEB_SERVER = startMockWebServer();
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

    @AfterEach
    void resetMockWebServer() {
        MOCK_SERVER_DISPATCHER.clear();
    }

    protected void enqueueJsonResponse(String path, Object body) {
        enqueueResponse(
                path,
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .setBody(toJson(body))
        );
    }

    protected void enqueueResponse(String path, MockResponse response) {
        MOCK_SERVER_DISPATCHER.enqueue(path, response);
    }

    protected RecordedRequest takeMockServerRequest() throws InterruptedException {
        return MOCK_WEB_SERVER.takeRequest(1, TimeUnit.SECONDS);
    }

    private static MockWebServer startMockWebServer() {
        MockWebServer server = new MockWebServer();
        server.setDispatcher(MOCK_SERVER_DISPATCHER);
        try {
            server.start(MOCK_WEB_SERVER_PORT);
            return server;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
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
}
