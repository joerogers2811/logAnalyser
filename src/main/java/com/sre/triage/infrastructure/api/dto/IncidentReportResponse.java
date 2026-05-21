package com.sre.triage.infrastructure.api.dto;

import com.sre.triage.domain.model.IncidentStatus;
import com.sre.triage.domain.model.TriageReport;
import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;

import java.time.Instant;
import java.util.UUID;

public record IncidentReportResponse(
        UUID id,
        String serviceName,
        String environment,
        Instant detectedAt,
        IncidentStatus status,
        TriageReport report
) {

    public static IncidentReportResponse fromEntity(IncidentEntity incident) {
        return new IncidentReportResponse(
                incident.getId(),
                incident.getServiceName(),
                incident.getEnvironment(),
                incident.getDetectedAt(),
                incident.getStatus(),
                incident.getTriageReport()
        );
    }
}
