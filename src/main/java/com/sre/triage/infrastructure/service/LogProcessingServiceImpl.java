package com.sre.triage.infrastructure.service;


import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.model.TriageReport;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.llm.client.GenAiClient;
import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;
import com.sre.triage.infrastructure.persistence.repository.IncidentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    private static final Logger log = LoggerFactory.getLogger(LogProcessingServiceImpl.class);
    private final IncidentRepository repository;
    private final GenAiClient genAiClient;

    public LogProcessingServiceImpl(IncidentRepository repository, GenAiClient genAiClient) {
        this.repository = repository;
        this.genAiClient = genAiClient;
    }

    @Override
    public Incident registerIncident(Incident incident) {
        log.info("Registering inbound incident for service: {}", incident.getServiceName());

        IncidentEntity entity = IncidentEntity.fromDomain(incident);
        repository.save(entity);

        return incident;
    }

    @Override
    @Async("triageTaskExecutor") // ◄── Explicitly delegates to our custom thread pool
    @Transactional
    public void processTriage(UUID incidentId) {
        log.info("Starting background async triage for incident ID: {}", incidentId);

        // 1. Fetch the raw record from the database
        IncidentEntity entity = repository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        try {
            // 2. Rehydrate the Domain aggregate to run domain logic safely
            Incident incident = entity.toDomain();

            // 3. Invoke the local LLM via Spring AI
            long startTime = System.currentTimeMillis();
            TriageReport report = genAiClient.analyzeLogs(incident.getRawLogDump());
            long duration = System.currentTimeMillis() - startTime;

            log.info("LLM inference completed in {}ms for incident {}", duration, incidentId);

            // 4. Update state through the strict domain boundary
            incident.applyTriageReport(report);

            // 5. Merge the state back into the DB Entity and save
            entity.updateFromDomain(incident);
            repository.save(entity);

            log.info("Incident {} successfully triaged and marked as TRIAGED", incidentId);

        } catch (Exception e) {
            log.error("Critical failure during async triage of incident {}", incidentId, e);
            entity.setStatus(com.sre.triage.domain.model.IncidentStatus.FAILED);
            repository.save(entity);
        }
    }

    @Override
    public Incident getIncident(UUID incidentId) {
        return repository.findById(incidentId)
                .map(IncidentEntity::toDomain)
                .orElse(null);
    }
}
