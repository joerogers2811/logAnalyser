package com.sre.triage.infrastructure.llm.dto;

import java.time.LocalDateTime;

public record OllamaRequest(String description, LocalDateTime timestamp) {
}
