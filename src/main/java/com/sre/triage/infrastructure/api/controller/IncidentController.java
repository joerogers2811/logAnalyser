package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.IncidentProcessor;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import com.sre.triage.infrastructure.api.dto.IncidentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    @Autowired
    private IncidentProcessor incidentProcessor;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@RequestBody IncidentRequest request) {
        Incident incident = new Incident(UUID.randomUUID(), request.getDescription(), LocalDateTime.now(), request.getCategory());
        TriageReport report = incidentProcessor.process(incident);
        return ResponseEntity.ok(new IncidentResponse(report.getId(), report.getSummary()));
    }

}
