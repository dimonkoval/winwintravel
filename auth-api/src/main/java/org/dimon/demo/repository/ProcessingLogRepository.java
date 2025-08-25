package org.dimon.demo.repository;

import org.dimon.demo.model.ProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProcessingLogRepository extends JpaRepository<ProcessingLog, UUID> {
}
