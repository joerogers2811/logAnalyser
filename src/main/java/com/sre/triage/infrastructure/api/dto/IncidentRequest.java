package com.sre.triage.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record IncidentRequest(
        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Service name must be alphanumeric, hyphens, or underscores")
        String serviceName,

        @NotBlank
        @Size(min = 2, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Environment must be alphanumeric, hyphens, or underscores")
        String environment,

        Instant timestamp,

        @NotBlank
        @Size(max = 100000, message = "Log dump exceeds maximum safety threshold of 100KB")
        String rawLogDump
) {}