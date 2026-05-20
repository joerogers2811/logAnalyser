package com.sre.triage.infrastructure.service;

import java.util.UUID;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.service.LogProcessingService;
import org.springframework.stereotype.Service;

@Service
public class LogProcessingServiceImpl implements LogProcessingService {

    @Override
    public void process(Incident rawLogs) {
        // Generate a random UUID for the job ID
        return;
    }

}
