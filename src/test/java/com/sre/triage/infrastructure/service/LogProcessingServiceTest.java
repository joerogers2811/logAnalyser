package com.sre.triage.infrastructure.service;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.model.IncidentCategory;
import com.sre.triage.domain.model.IncidentStatus;
import com.sre.triage.domain.model.TriageReport;
import com.sre.triage.infrastructure.llm.client.GenAiClient;
import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogProcessingServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private GenAiClient genAiClient;

    @InjectMocks
    private LogProcessingServiceImpl logProcessingService;

    @Test
    public void testRegisterIncident_SavesToDatabase() {
        // Arrange
        String serviceName = "service1";
        String environment = "env1";
        Instant detectedAt = Instant.now();
        String rawLogDump = "log dump";

        Incident incident = new Incident(serviceName, environment, detectedAt, rawLogDump);

        // Act
        logProcessingService.registerIncident(incident);

        // Assert
        verify(incidentRepository, times(1)).save(any(IncidentEntity.class));
    }

    @Test
    void testProcessTriage_Success() {
        UUID incidentId = UUID.randomUUID();
        Incident incident = new Incident("service", "prod", Instant.now(), "logs");
        IncidentEntity entity = IncidentEntity.fromDomain(incident);
        entity.setId(incidentId);

        TriageReport report = new TriageReport(IncidentCategory.DATABASE_TIMEOUT, "summary", "cause", "action", null);

        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(entity));
        when(genAiClient.analyzeLogs(any())).thenReturn(report);

        logProcessingService.processTriage(incidentId);

        verify(incidentRepository, times(1)).save(any(IncidentEntity.class)); 
        
        assertEquals(IncidentStatus.TRIAGED, entity.getStatus());
        assertEquals(report, entity.getTriageReport());
    }

    @Test
    void testProcessTriage_NotFound() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> logProcessingService.processTriage(incidentId));
    }

    @Test
    void testProcessTriage_Failure() {
        UUID incidentId = UUID.randomUUID();
        Incident incident = new Incident("service", "prod", Instant.now(), "logs");
        IncidentEntity entity = IncidentEntity.fromDomain(incident);
        entity.setId(incidentId);

        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(entity));
        when(genAiClient.analyzeLogs(any())).thenThrow(new RuntimeException("LLM error"));

        logProcessingService.processTriage(incidentId);

        verify(incidentRepository).save(entity);
        assertEquals(IncidentStatus.FAILED, entity.getStatus());
    }
}
