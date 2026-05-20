package com.sre.triage.infrastructure.service;


import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    private static final Logger log = LoggerFactory.getLogger(LogProcessingServiceImpl.class);
    private final IncidentRepository repository;

    public LogProcessingServiceImpl(IncidentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Incident registerIncident(Incident incident) {
        log.info("Registering inbound incident for service: {}", incident.getServiceName());

        IncidentEntity entity = IncidentEntity.fromDomain(incident);
        repository.save(entity);

        return incident;
    }

    @Override
    public void processTriage(UUID incidentId) {

    }
}
