package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.IncidentCategory;
import com.sre.triage.domain.model.IncidentStatus;
import com.sre.triage.domain.model.TriageReport;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentReportResponse;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import com.sre.triage.infrastructure.service.LogQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    private static final String SUBMIT_ENDPOINT = "/logs/api/v1/analyser/submit";
    private static final String GET_ENDPOINT = "/logs/api/v1/analyser/get";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogProcessingService logProcessingService;

    @MockitoBean
    private LogQueryService logQueryService;

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
        callSubmit(jsonPayload)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.jobId").value(matchesPattern(uuidRegex)));
    }

    @Test
    public void shouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidPayload = "invalid-payload";

        callSubmit(invalidPayload).andExpect(status().isBadRequest());
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
        callSubmit(jsonPayload).andExpect(status().isBadRequest());
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
        callSubmit(jsonPayload).andExpect(status().isBadRequest());
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
        callSubmit(jsonPayload).andExpect(status().isBadRequest());
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
        callSubmit(jsonPayload)
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
       callSubmit(jsonPayload)
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
        callSubmit(jsonPayload)
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnTriageReportForValidId() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TriageReport.LlmTelemetry telemetry = new TriageReport.LlmTelemetry(100, Duration.ofMillis(500), 200.0);
        TriageReport expectedReport = new TriageReport(
                IncidentCategory.DATABASE_TIMEOUT,
                "High impact",
                "DB connection pool exhausted",
                "Increase pool size",
                telemetry
        );

        IncidentReportResponse expectedIncident = new IncidentReportResponse(
                 id,
                "payment-gateway",
                "production",
                Instant.parse("2026-05-19T17:42:00Z"),
                IncidentStatus.TRIAGED,
                expectedReport
        );

        when(logQueryService.getIncidentReport(id)).thenReturn(Optional.of(expectedIncident));

        // Act & Assert
        callRetrieve(id.toString())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.serviceName").value("payment-gateway"))
                .andExpect(jsonPath("$.report.category").value("DATABASE_TIMEOUT"))
                .andExpect(jsonPath("$.report.impactSummary").value("High impact"))
                .andExpect(jsonPath("$.report.rootCause").value("DB connection pool exhausted"))
                .andExpect(jsonPath("$.report.recommendedAction").value("Increase pool size"))
                .andExpect(jsonPath("$.report.telemetry.tokenCount").value(100));
    }

    @Test
    public void shouldReturnNotFoundForMissingId() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(logQueryService.getIncidentReport(
                id)).thenReturn(Optional.empty());

        // Act & Assert
        callRetrieve(id.toString())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequestForInvalidUuid() throws Exception {
        // Act & Assert
        callRetrieve("not-a-uuid")
                .andExpect(status().isBadRequest());
    }


    private ResultActions callSubmit(String jsonPayload) throws Exception {
        return mockMvc.perform(post(SUBMIT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload));
    }

    private ResultActions callRetrieve(String id) throws Exception {
        return mockMvc.perform(get(GET_ENDPOINT + "/" + id));
    }
}
