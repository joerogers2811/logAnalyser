package com.sre.triage.infrastructure.service;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.infrastructure.api.dto.IncidentReportResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogQueryServiceTest {

    @Mock
    private IncidentRepository logRepository;

    @InjectMocks
    private LogQueryService logQueryService;

    @Test
    void shouldReturnReportWhenIncidentExists() {
        UUID id = UUID.randomUUID();
        Incident incident = new Incident("service", "prod", Instant.now(), "logs");
        IncidentEntity entity = IncidentEntity.fromDomain(incident);
        // Ensure ID is same as generated UUID
        entity.setId(id);

        when(logRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<IncidentReportResponse> result = logQueryService.getIncidentReport(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        assertEquals("service", result.get().serviceName());
    }

    @Test
    void shouldReturnEmptyWhenIncidentDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(logRepository.findById(id)).thenReturn(Optional.empty());

        Optional<IncidentReportResponse> result = logQueryService.getIncidentReport(id);

        assertFalse(result.isPresent());
    }
}
