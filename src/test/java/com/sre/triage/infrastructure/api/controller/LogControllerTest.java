package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.service.LogProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
        String validPayload = "{\n" +
                "  \"serviceName\": \"payment-gateway\",\n" +
                "  \"environment\": \"production\",\n" +
                "  \"timestamp\": \"2026-05-19T17:42:00Z\",\n" +
                "  \"rawLogDump\": \"2026-05-19 17:42:01.104 ERROR [payment-gateway,7f3b89,2a11] 42105 --- [nio-8080-exec-4] c.e.p.service.PaymentProcessor : Failed to settle transaction tx_99482\\norg.postgresql.util.PSQLException: Connection to localhost:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.\\n\tat org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:342)\\n\tat org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:54)\\n\tat org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:273)\\n\tat org.postgresql.Driver.makeConnection(Driver.java:446)\"\n" +
                "}";

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload))
               .andExpect(status().isAccepted())
               .andExpect(content().json("{\"jobId\":\"job-12345\"}")); // Replace with actual expected job ID
    }

    @Test
    public void shouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidPayload = "{\n" +
                "  \"serviceName\": \"payment-gateway\",\n" +
                "  \"environment\": \"production\",\n" +
                "  \"timestamp\": \"2026-05-19T17:42:00Z\",\n" +
                "  \"rawLogDump\": \"Invalid log data\"\n" +
                "}";

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
               .andExpect(status().isBadRequest());
    }
}
