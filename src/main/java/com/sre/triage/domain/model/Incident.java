package com.sre.triage.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Incident {

    private final UUID id;
    private final String serviceName;
    private final String environment;
    private final Instant detectedAt;
    private final String rawLogDump;
    private IncidentStatus status;
    private TriageReport report;

    public Incident(String serviceName, String environment, Instant detectedAt, String rawLogDump) {
        this.id = UUID.randomUUID();
        this.serviceName = validateNotBlank(serviceName, "serviceName");
        this.environment = validateNotBlank(environment, "environment");
        this.detectedAt = detectedAt != null ? detectedAt : Instant.now();
        this.rawLogDump = validateNotBlank(rawLogDump, "rawLogDump");
        this.status = IncidentStatus.PENDING;
    }

    public void applyTriageReport(TriageReport report){
        if (this.status != IncidentStatus.PENDING){
            throw  new IllegalStateException("Triage report already applied");
        }
        this.report = report;
        this.status = IncidentStatus.TRIAGED;
    }

    private String validateNotBlank(String toValidate, String field) {
        if (toValidate == null || toValidate.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return toValidate.strip();
    }
}
