package com.sre.triage.infrastructure.api.dto;

import com.sre.triage.domain.model.IncidentCategory;
import java.time.LocalDateTime;

public record IncidentRequest(String description, LocalDateTime timestamp, IncidentCategory category) {
}
