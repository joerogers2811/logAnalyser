package com.sre.triage.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record IncidentRequest(
        @Schema(description = "Name of the service where the incident occurred", example = "payment-service")
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Service name must be alphanumeric, hyphens, or underscores")
        String serviceName,

        @Schema(description = "Environment where the incident occurred", example = "production")
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Environment must be alphanumeric, hyphens, or underscores")
        String environment,

        @Schema(description = "Timestamp of the incident", example = "2026-05-21T13:17:00Z")
        Instant timestamp,

        @Schema(description = "Raw log dump associated with the incident", example = "ERROR: connection refused at ...")
        @NotBlank
        @Size(max = 100000, message = "Log dump exceeds maximum safety threshold of 100KB")
        String rawLogDump
) {}