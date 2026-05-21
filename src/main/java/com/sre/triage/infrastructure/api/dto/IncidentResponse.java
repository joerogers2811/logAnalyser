package com.sre.triage.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record IncidentResponse(
        @Schema(description = "The unique identifier of the submitted incident job", example = "550e8400-e29b-41d4-a716-446655440000")
        String jobId
) {
}
