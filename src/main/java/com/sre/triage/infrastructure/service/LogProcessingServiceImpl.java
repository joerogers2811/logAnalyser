package com.sre.triage.infrastructure.service;


import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    private final IncidentRepository repository;

    public LogProcessingServiceImpl(IncidentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Incident registerIncident(Incident incident) {
        return null;
    }

    @Override
    public void processTriage(UUID incidentId) {

    }
}
