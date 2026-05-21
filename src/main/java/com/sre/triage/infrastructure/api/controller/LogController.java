package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentReportResponse;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import com.sre.triage.infrastructure.api.dto.IncidentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sre.triage.infrastructure.service.LogQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/logs/api/v1/analyser")
@Tag(name = "Log Analysis API", description = "Endpoints for submitting logs for AI-powered triage and retrieving analysis reports")
public class LogController {

    @Autowired
    private LogProcessingService logProcessingService;
    @Autowired
    private LogQueryService queryService;

    @PostMapping("/submit")
    @Operation(summary = "Submit an incident for analysis", description = "Asynchronously processes the provided log dump and generates a triage report using AI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Incident accepted and processing started"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
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
    @Operation(summary = "Get triage report", description = "Retrieves the current status and the generated AI triage report for a specific incident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found"),
            @ApiResponse(responseCode = "400", description = "Invalid UUID format")
    })
    public ResponseEntity<IncidentReportResponse> getReport(
            @Parameter(description = "The unique UUID of the incident", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
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
