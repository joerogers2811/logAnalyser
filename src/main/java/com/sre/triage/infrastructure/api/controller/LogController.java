package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentReportResponse;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import com.sre.triage.infrastructure.api.dto.IncidentResponse;
import com.sre.triage.infrastructure.service.LogQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/logs/api/v1/analyser")
public class LogController {

    @Autowired
    private LogProcessingService logProcessingService;
    @Autowired
    private LogQueryService queryService;

    @PostMapping("/submit")
    public ResponseEntity<IncidentResponse> createJob(@RequestBody IncidentRequest incidentRequest) {
        try {
            Incident incident = toIncident(incidentRequest);

            logProcessingService.registerIncident(incident);
            logProcessingService.processTriage(incident.getId());

            return new ResponseEntity<>(new IncidentResponse(incident.getId().toString()), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<IncidentReportResponse> getReport(@PathVariable UUID id) {
        try {
            return queryService.getIncidentReport(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private Incident toIncident(IncidentRequest incidentRequest) {
        return new Incident(
                incidentRequest.serviceName(),
                incidentRequest.environment(),
                incidentRequest.timestamp(),
                incidentRequest.rawLogDump()
        );
    }

}
