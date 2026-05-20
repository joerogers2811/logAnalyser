package com.sre.triage.infrastructure.api.dto;

public record IncidentRequest(
        String serviceName,
        String environment,
        java.time.Instant timestamp,
        String rawLogDump
) {
}
