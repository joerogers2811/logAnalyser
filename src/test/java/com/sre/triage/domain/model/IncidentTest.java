package com.sre.triage.domain.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class IncidentTest {

    @Test
    void shouldCreateIncidentWithValidData() {
        String serviceName = "payment-service";
        String environment = "production";
        String rawLogDump = "java.lang.NullPointerException";
        Instant now = Instant.now();

        Incident incident = new Incident(serviceName, environment, now, rawLogDump);

        assertNotNull(incident.getId());
        assertEquals(serviceName, incident.getServiceName());
        assertEquals(environment, incident.getEnvironment());
        assertEquals(now, incident.getDetectedAt());
        assertEquals(rawLogDump, incident.getRawLogDump());
        assertEquals(IncidentStatus.PENDING, incident.getStatus());
        assertNull(incident.getReport());
    }

    @Test
    void shouldThrowExceptionWhenServiceNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Incident("", "prod", Instant.now(), "logs"));
    }

    @Test
    void shouldThrowExceptionWhenEnvironmentIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Incident("service", "  ", Instant.now(), "logs"));
    }

    @Test
    void shouldThrowExceptionWhenRawLogDumpIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Incident("service", "prod", Instant.now(), null));
    }

    @Test
    void shouldApplyTriageReportSuccessfully() {
        Incident incident = new Incident("service", "prod", Instant.now(), "logs");
        TriageReport report = new TriageReport(
            IncidentCategory.DATABASE_TIMEOUT, "summary", "cause", "action", null);

        incident.applyTriageReport(report);

        assertEquals(IncidentStatus.TRIAGED, incident.getStatus());
        assertEquals(report, incident.getReport());
    }

    @Test
    void shouldThrowExceptionWhenApplyingReportTwice() {
        Incident incident = new Incident("service", "prod", Instant.now(), "logs");
        TriageReport report = new TriageReport(
            IncidentCategory.DATABASE_TIMEOUT, "summary", "cause", "action", null);

        incident.applyTriageReport(report);

        assertThrows(IllegalStateException.class, () -> incident.applyTriageReport(report));
    }

    @Test
    void shouldDefaultDetectedAtIfNull() {
        Incident incident = new Incident("service", "prod", null, "logs");
        assertNotNull(incident.getDetectedAt());
    }
}
