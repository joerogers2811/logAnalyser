package com.sre.triage.infrastructure.api.controller;

import com.sre.triage.domain.service.LogProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogProcessingService logProcessingService;

    @PostMapping("/api/v1/analyzer/jobs")
    public ResponseEntity<String> createJob() {
        String rawLogs = "Fixed string for testing"; // Fixed string to pass to the service
        String jobId = logProcessingService.process(rawLogs);
        return new ResponseEntity<>("{\"jobId\":\"" + jobId + "\"}", HttpStatus.ACCEPTED);
    }
}
