package com.sre.triage.domain.service;

import com.sre.triage.infrastructure.service.LogProcessingServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogProcessingServiceTest {


    @Test
    public void shouldProcessLogsAndReturnJobId() {
        LogProcessingService service = new LogProcessingServiceImpl();
        String rawLogs = "Sample log data";
        String jobId = service.process(rawLogs);
        
        assertEquals("job-12345", jobId); // Replace with actual expected job ID
    }
}
