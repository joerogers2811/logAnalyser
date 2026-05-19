package com.sre.triage.domain.service;

import com.sre.triage.domain.model.Incident;
import com.sre.triage.domain.model.TriageReport;

public interface IncidentProcessor {
    TriageReport process(Incident incident);
}
