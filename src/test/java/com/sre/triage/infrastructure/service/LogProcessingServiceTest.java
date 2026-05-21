package com.sre.triage.infrastructure.service;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogProcessingServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private LogProcessingServiceImpl logProcessingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
}
