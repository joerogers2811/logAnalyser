package com.sre.triage.infrastructure.llm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OllamaResponse(UUID id, String summary) {
}
