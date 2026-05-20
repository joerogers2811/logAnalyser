package com.sre.triage.domain.service;

import com.sre.triage.domain.model.Incident;

import java.util.UUID;

public interface LogProcessingService {

    /**
     * Accepts a raw incident, saves it immediately, and drops it into
     * the asynchronous triage pipeline.
     */
    Incident registerIncident(Incident incident);

    /**
     * The background task that orchestrates the AI analysis,
     * updates the incident state, and persists the result.
     */
    void processTriage(UUID incidentId);
}
