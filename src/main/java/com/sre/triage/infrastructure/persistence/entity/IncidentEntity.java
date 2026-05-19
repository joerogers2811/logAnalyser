package com.sre.triage.infrastructure.persistence.entity;

import com.sre.triage.domain.model.IncidentCategory;
import java.time.LocalDateTime;
import java.util.UUID;

public class IncidentEntity {

    private UUID id;
    private String description;
    private LocalDateTime timestamp;
    private IncidentCategory category;

    // Getters and setters

}
