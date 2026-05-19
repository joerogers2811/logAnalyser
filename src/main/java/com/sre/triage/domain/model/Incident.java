package com.sre.triage.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Incident(UUID id, String description, LocalDateTime timestamp, IncidentCategory category) {
}
