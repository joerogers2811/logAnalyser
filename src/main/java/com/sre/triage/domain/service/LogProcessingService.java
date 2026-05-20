package com.sre.triage.domain.service;

import com.sre.triage.domain.model.Incident;

public interface LogProcessingService {

    void process(Incident rawLogs);
}
