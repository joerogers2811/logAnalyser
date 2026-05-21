package com.sre.triage.infrastructure.service;

import com.sre.triage.infrastructure.api.dto.IncidentReportResponse;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LogQueryService {

    @Autowired
    private final IncidentRepository logRepository;

    public LogQueryService(IncidentRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Retrieves the full incident for a given incident ID.
     */
    public Optional<IncidentReportResponse> getIncidentReport(UUID incidentId) {
        return logRepository.findById(incidentId)
                .map(IncidentReportResponse::fromEntity);
    }
}
