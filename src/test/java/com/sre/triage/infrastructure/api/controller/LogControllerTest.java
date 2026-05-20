package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogProcessingService logProcessingService;

    @BeforeEach
    public void setUp() {
        // Set up any initial data if needed
    }

    @Test
    public void shouldReturn202AndJobIdForValidPayload() throws Exception {

        String realisticLog = """
        2026-05-19 17:42:01.104 ERROR [payment-gateway,7f3b89,2a11] 42105 --- c.e.p.service.PaymentProcessor : Failed
        org.postgresql.util.PSQLException: Connection to localhost:5432 refused.
            at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:342)
        """;

        // 2. Build your Java payload DTO record
        IncidentRequest request = new IncidentRequest(
                "payment-gateway",
                "production",
                Instant.parse("2026-05-19T17:42:00Z"),
                realisticLog
        );

        // 3. Let ObjectMapper turn it into valid, escaped JSON automatically
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Standard UUID regex pattern (v1 through v5 matching)
        String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").value(matchesPattern(uuidRegex)));
    }

    @Test
    public void shouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidPayload = "invalid-payload";

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForNullServiceName() throws Exception {
        // Arrange
        String serviceName = null;
        String environment = "production";
        Instant timestamp = Instant.now();
        String rawLogDump = "log dump content";

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForEmptyServiceName() throws Exception {
        // Arrange
        String serviceName = "";
        String environment = "production";
        Instant timestamp = Instant.now();
        String rawLogDump = "log dump content";

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForNullEnvironment() throws Exception {
        // Arrange
        String serviceName = "service1";
        String environment = null;
        Instant timestamp = Instant.now();
        String rawLogDump = "log dump content";

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForEmptyEnvironment() throws Exception {
        // Arrange
        String serviceName = "service1";
        String environment = "";
        Instant timestamp = Instant.now();
        String rawLogDump = "log dump content";

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForNullRawLogDump() throws Exception {
        // Arrange
        String serviceName = "service1";
        String environment = "production";
        Instant timestamp = Instant.now();
        String rawLogDump = null;

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForEmptyRawLogDump() throws Exception {
        // Arrange
        String serviceName = "service1";
        String environment = "production";
        Instant timestamp = Instant.now();
        String rawLogDump = "";

        IncidentRequest request = new IncidentRequest(serviceName, environment, timestamp, rawLogDump);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isBadRequest());
    }
}
