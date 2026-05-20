package com.sre.triage.infrastructure.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturn202AndJobId() throws Exception {
        String jobId = "job-12345"; // Replace with actual job ID generation logic

        mockMvc.perform(post("/logs/api/v1/analyzer/jobs")
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isAccepted())
               .andExpect(content().json("{\"jobId\":\"" + jobId + "\"}"));
    }
}
