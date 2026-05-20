package com.sre.triage.infrastructure.persistence.repository;

import com.sre.triage.infrastructure.persistence.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<IncidentEntity, UUID> {

}
