package com.sre.triage.infrastructure.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentResponse(UUID id, String summary) {
}
