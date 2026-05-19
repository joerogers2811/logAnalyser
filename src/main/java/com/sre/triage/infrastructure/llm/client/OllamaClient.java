package com.sre.triage.infrastructure.llm.client;

import com.sre.triage.infrastructure.llm.dto.OllamaRequest;
import com.sre.triage.infrastructure.llm.dto.OllamaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama")
public class OllamaClient {

    @PostMapping
    public ResponseEntity<OllamaResponse> processRequest(@RequestBody OllamaRequest request) {
        // Simulate processing
        String summary = "Processed: " + request.getDescription();
        return ResponseEntity.ok(new OllamaResponse(UUID.randomUUID(), summary));
    }

}
