package com.sre.triage.infrastructure.api.dto;

import java.time.ZonedDateTime;

public record IncidentRequest(
        String serviceName,
        String environment,
        ZonedDateTime timestamp,
        String rawLogDump
) {
}
