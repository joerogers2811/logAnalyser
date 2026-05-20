package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogProcessingService logProcessingService;

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

        String expectedJobId = "job-12345";
        when(logProcessingService.process(realisticLog)).thenReturn(expectedJobId);

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
               .andExpect(status().isAccepted())
               .andExpect(content().json("{\"jobId\":\"" + expectedJobId + "\"}"));
    }

    @Test
    public void shouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidPayload = "invalid-payload";

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
               .andExpect(status().isBadRequest());
    }
}
