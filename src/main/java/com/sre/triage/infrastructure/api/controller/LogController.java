package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.model.TriageReport;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import com.sre.triage.infrastructure.api.dto.IncidentResponse;
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

    @PostMapping("/submit")
    public ResponseEntity<IncidentResponse> createJob(@RequestBody IncidentRequest incidentRequest) {
        try {
            Incident incident = new Incident(
                    incidentRequest.serviceName(),
                    incidentRequest.environment(),
                    incidentRequest.timestamp(),
                    incidentRequest.rawLogDump());

            logProcessingService.registerIncident(incident);
            logProcessingService.processTriage(incident.getId());

            return new ResponseEntity<>(new IncidentResponse(incident.getId().toString()), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Incident> getReport(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Incident incident = logProcessingService.getIncident(uuid);
            if (incident == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(incident, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
