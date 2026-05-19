package com.sre.triage.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record TriageReport(UUID id, String summary, LocalDateTime timestamp) {
}
