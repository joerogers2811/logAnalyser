package com.sre.triage.infrastructure.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    @PostMapping("/api/v1/analyzer/jobs")
    public ResponseEntity<String> createJob() {
        String jobId = "job-12345"; // Replace with actual job ID generation logic
        return new ResponseEntity<>("{\"jobId\":\"" + jobId + "\"}", HttpStatus.ACCEPTED);
    }
}
