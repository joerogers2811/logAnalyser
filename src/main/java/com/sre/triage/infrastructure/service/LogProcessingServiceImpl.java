package com.sre.triage.infrastructure.service;


import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    @Override
    public Incident registerIncident(Incident incident) {
        return null;
    }

    @Override
    public void processTriage(UUID incidentId) {

    }
}
