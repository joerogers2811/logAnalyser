package com.sre.triage.domain.model;

import java.time.Duration;

public record TriageReport(
        IncidentCategory category,
        String impactSummary,
        String rootCause,
        String recommendedAction,
        LlmTelemetry telemetry
) {
    // Nested value object for performance tracking
    public record LlmTelemetry(
            long tokenCount,
            Duration inferenceDuration,
            double tokensPerSecond
    ) {}
}