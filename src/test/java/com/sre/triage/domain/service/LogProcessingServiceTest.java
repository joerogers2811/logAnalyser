package com.sre.triage.domain.service;

import com.sre.triage.infrastructure.service.LogProcessingServiceImpl;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogProcessingServiceTest {

    @Test
    public void shouldProcessLogsAndReturnJobId() {
        LogProcessingService service = new LogProcessingServiceImpl();
        String rawLogs = "Sample log data";
        String jobId = service.process(rawLogs);
        
        assertTrue(UUID.fromString(jobId), "The returned job ID is not a valid UUID");
    }
}
