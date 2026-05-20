package com.sre.triage.infrastructure.persistence.entity;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.model.IncidentStatus;
import com.sre.triage.domain.model.TriageReport;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class IncidentEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "environment", nullable = false, length = 50)
    private String environment;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    // Stored as a large text object to comfortably hold full stack traces
    @Lob
    @Column(name = "raw_log_dump", nullable = false)
    private String rawLogDump;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private IncidentStatus status;

    // Maps our complex record directly to a native PostgreSQL JSONB / standard JSON column
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "triage_report", columnDefinition = "jsonb")
    private TriageReport triageReport;

    // --- Constructors ---

    // Required by Hibernate specs - kept protected so developers use mapping factory methods instead
    protected IncidentEntity() {}

    // --- Domain Mapping Factory Methods (The Bridge) ---

    /**
     * Converts a pure Domain Aggregate into a persistent Database Entity (Ingestion)
     */
    public static IncidentEntity fromDomain(Incident incident) {
        IncidentEntity entity = new IncidentEntity();
        entity.id = incident.getId();
        entity.serviceName = incident.getServiceName();
        entity.environment = incident.getEnvironment();
        entity.detectedAt = incident.getDetectedAt();
        entity.rawLogDump = incident.getRawLogDump();
        entity.status = incident.getStatus();
        entity.triageReport = incident.getReport();
        return entity;
    }

    /**
     * Rehydrates an Entity back into a valid, state-protected Domain Aggregate
     */
    public Incident toDomain() {
        Incident incident = new Incident(
                this.serviceName,
                this.environment,
                this.detectedAt,
                this.rawLogDump
        );

        // Force-override the dynamic state fields back into the rehydrated instance
        if (this.triageReport != null) {
            incident.applyTriageReport(this.triageReport);
        }

        return incident;
    }

    /**
     * Merges structural modifications from the domain back onto the managed entity state
     */
    public void updateFromDomain(Incident incident) {
        this.status = incident.getStatus();
        this.triageReport = incident.getReport();
    }

    // --- Standard Getters & Setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }

    public String getRawLogDump() { return rawLogDump; }
    public void setRawLogDump(String rawLogDump) { this.rawLogDump = rawLogDump; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public TriageReport getTriageReport() { return triageReport; }
    public void setTriageReport(TriageReport triageReport) { this.triageReport = triageReport; }
}