package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.service.LogProcessingService;
import com.sre.triage.infrastructure.api.dto.IncidentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogProcessingService logProcessingService;

    @PostMapping("/api/v1/analyzer/jobs")
    public ResponseEntity<String> createJob(@RequestBody IncidentRequest incidentRequest) {
        try {
            String jobId = logProcessingService.process(incidentRequest.rawLogDump());
            return new ResponseEntity<>("{\"jobId\":\"" + jobId + "\"}", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
